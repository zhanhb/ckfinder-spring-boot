package com.github.zhanhb.ckfinder.connector.autoconfigure;

import com.github.zhanhb.ckfinder.connector.ConnectorServlet;
import com.github.zhanhb.ckfinder.connector.api.AccessControl;
import com.github.zhanhb.ckfinder.connector.api.BasePathBuilder;
import com.github.zhanhb.ckfinder.connector.api.CKFinderContext;
import com.github.zhanhb.ckfinder.connector.api.ImageProperties;
import com.github.zhanhb.ckfinder.connector.api.License;
import com.github.zhanhb.ckfinder.connector.api.LicenseFactory;
import com.github.zhanhb.ckfinder.connector.api.Plugin;
import com.github.zhanhb.ckfinder.connector.api.ThumbnailProperties;
import com.github.zhanhb.ckfinder.connector.plugins.FileEditor;
import com.github.zhanhb.ckfinder.connector.plugins.ImageResize;
import com.github.zhanhb.ckfinder.connector.plugins.ImageResizeParam;
import com.github.zhanhb.ckfinder.connector.plugins.ImageResizeSize;
import com.github.zhanhb.ckfinder.connector.plugins.Watermark;
import com.github.zhanhb.ckfinder.connector.plugins.WatermarkSettings;
import com.github.zhanhb.ckfinder.connector.support.AccessControlLevel;
import com.github.zhanhb.ckfinder.connector.support.DefaultCKFinderContext;
import com.github.zhanhb.ckfinder.connector.support.DefaultPathBuilder;
import com.github.zhanhb.ckfinder.connector.support.DefaultPluginRegistry;
import com.github.zhanhb.ckfinder.connector.support.DefaultResourceType;
import com.github.zhanhb.ckfinder.connector.support.FixLicenseFactory;
import com.github.zhanhb.ckfinder.connector.support.HostLicenseFactory;
import com.github.zhanhb.ckfinder.connector.support.InMemoryAccessController;
import com.github.zhanhb.ckfinder.connector.support.KeyGenerator;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import com.github.zhanhb.ckfinder.connector.utils.PathUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.github.zhanhb.ckfinder.connector.api.Constants.DEFAULT_BASE_URL;

/**
 *
 * @author zhanhb
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(CKFinderProperties.class)
@SuppressWarnings("PublicInnerClass")
@ConditionalOnProperty(prefix = CKFinderProperties.CKFINDER_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class CKFinderAutoConfiguration {

  @Configuration
  @ConditionalOnMissingBean(BasePathBuilder.class)
  public static class DefaultBasePathBuilderConfigurer {

    @Bean
    public DefaultPathBuilder defaultPathBuilder(ApplicationContext applicationContext, CKFinderProperties properties) {
      ServletContext servletContext = applicationContext.getBean(ServletContext.class);
      CKFinderProperties.Base base = properties.getBase();
      String baseUrl = base.getUrl();
      baseUrl = PathUtils.addSlashToBegin(PathUtils.addSlashToEnd(
              StringUtils.hasLength(baseUrl) ? baseUrl : DEFAULT_BASE_URL));
      String basePath = base.getPath();
      basePath = StringUtils.hasLength(basePath) ? basePath : baseUrl.replaceAll("^/+", "");
      Path path = Paths.get(basePath).normalize();
      try {
        if (!path.isAbsolute()) {
          basePath = servletContext.getRealPath(basePath);
          if (basePath != null) {
            path = Paths.get(basePath).normalize();
          }
        }
      } catch (IllegalArgumentException ignored) {
      }
      return DefaultPathBuilder.builder().basePath(path).baseUrl(servletContext.getContextPath() + PathUtils.addSlashToEnd(baseUrl)).build();
    }

  }

  @Configuration
  @ConditionalOnMissingBean(AccessControl.class)
  public static class DefaultAccessControlConfigurer {

    private static int calc(int old, boolean condition, int mask) {
      return condition ? old | mask : old & ~mask;
    }

    @Bean
    public InMemoryAccessController defaultAccessControl(CKFinderProperties properties) {
      InMemoryAccessController accessControl = new InMemoryAccessController();
      CKFinderProperties.AccessControl[] accessControls = properties.getAccessControls();
      if (accessControls != null) {
        for (CKFinderProperties.AccessControl ac : accessControls) {
          String role = ac.getRole();
          String resourceType = ac.getResourceType();
          String folder = ac.getFolder();
          int mask = 0;
          mask = calc(mask, ac.isFileDelete(), AccessControl.FILE_DELETE);
          mask = calc(mask, ac.isFileRename(), AccessControl.FILE_RENAME);
          mask = calc(mask, ac.isFileUpload(), AccessControl.FILE_UPLOAD);
          mask = calc(mask, ac.isFileView(), AccessControl.FILE_VIEW);
          mask = calc(mask, ac.isFolderCreate(), AccessControl.FOLDER_CREATE);
          mask = calc(mask, ac.isFolderDelete(), AccessControl.FOLDER_DELETE);
          mask = calc(mask, ac.isFolderRename(), AccessControl.FOLDER_RENAME);
          mask = calc(mask, ac.isFolderView(), AccessControl.FOLDER_VIEW);

          AccessControlLevel accessControlLevel = AccessControlLevel
                  .builder().role(role).resourceType(resourceType).folder(folder)
                  .mask(mask).build();
          accessControl.addPermission(accessControlLevel);
        }
      }
      return accessControl;
    }

  }

  @Configuration
  @ConditionalOnMissingBean(CKFinderContext.class)
  public static class DefaultConfigurationConfigurer {

    private static String toString(String[] array) {
      return String.join(",", array);
    }

    @Bean
    public CKFinderContext ckfinderConfiguration(CKFinderProperties properties,
            BasePathBuilder basePathBuilder,
            AccessControl defaultAccessControl,
            ObjectProvider<List<Plugin>> pluginsProvider,
            ObjectProvider<List<CKFinderContextCustomizer>> customizersProvider) {
      List<Plugin> plugins = pluginsProvider.getIfAvailable();
      DefaultCKFinderContext.Builder builder = DefaultCKFinderContext.builder()
              .enabled(properties.getConnector().isEnabled())
              .licenseFactory(createLiceFactory(properties.getLicense()));
      CKFinderProperties.Image image = properties.getImage();
      builder.image(ImageProperties.builder().maxWidth(image.getMaxWidth())
              .maxHeight(image.getMaxHeight())
              .quality(image.getQuality())
              .build());
      if (properties.getDefaultResourceTypes() != null) {
        builder.defaultResourceTypes(Arrays.asList(properties.getDefaultResourceTypes()));
      }
      if (properties.getUserRoleSessionVar() != null) {
        builder.userRoleName(properties.getUserRoleSessionVar());
      }
      builder.csrfProtectionEnabled(properties.isCsrf()).accessControl(defaultAccessControl);
      ThumbnailProperties thumbnail = createThumbs(properties.getThumbs(), basePathBuilder);
      builder.thumbnail(thumbnail)
              .disallowUnsafeCharacters(properties.isDisallowUnsafeCharacters())
              .doubleFileExtensionsDisallowed(!properties.isAllowDoubleExtension())
              .checkSizeAfterScaling(properties.isCheckSizeAfterScaling())
              .secureImageUploads(properties.isSecureImageUploads());
      if (properties.getTypes() != null) {
        setTypes(builder, basePathBuilder, properties.getTypes(), Optional.ofNullable(thumbnail).map(ThumbnailProperties::getPath).orElse(null));
      }
      builder.forceAscii(properties.isForceAscii());
      if (properties.getHiddenFolders() != null) {
        builder.hiddenFolders(Arrays.asList(properties.getHiddenFolders()));
      }
      if (properties.getHiddenFiles() != null) {
        builder.hiddenFiles(Arrays.asList(properties.getHiddenFiles()));
      }
      DefaultPluginRegistry registry = DefaultPluginRegistry.newInstance();
      if (plugins != null) {
        plugins.forEach(plugin -> plugin.register(registry));
      }
      builder.events(registry.buildEventHandler()).
              commandFactory(registry.buildCommandFactory())
              .publicPluginNames(registry.getPluginNames())
              .pluginInfos(registry.getPluginInfos());
      List<CKFinderContextCustomizer> customizers = customizersProvider.getIfAvailable();
      if (customizers != null) {
        for (CKFinderContextCustomizer customizer : customizers) {
          builder = customizer.custom(builder);
        }
      }
      return builder.build();
    }

    private void setTypes(DefaultCKFinderContext.Builder builder,
            BasePathBuilder basePathBuilder, Map<String, CKFinderProperties.Type> types,
            @Nullable Path rootThumbnail) {
      Path basePath = Objects.requireNonNull(basePathBuilder.getBasePath(), "ckfinder base path");
      String baseUrl = Objects.requireNonNull(basePathBuilder.getBaseUrl(), "ckfinder base url");
      for (Map.Entry<String, CKFinderProperties.Type> entry : types.entrySet()) {
        final String typeName = entry.getKey();
        CKFinderProperties.Type type = entry.getValue();
        Assert.hasText(typeName, "Resource type name should not be empty");
        DefaultResourceType.Builder resourceType = DefaultResourceType.builder()
                .name(typeName);

        if (type.getAllowedExtensions() != null) {
          resourceType.allowedExtensions(toString(type.getAllowedExtensions()));
        }
        if (type.getDeniedExtensions() != null) {
          resourceType.deniedExtensions(toString(type.getDeniedExtensions()));
        }
        String path = StringUtils.hasLength(type.getDirectory()) ? type.getDirectory() : typeName.toLowerCase();
        String url = StringUtils.hasLength(type.getUrl()) ? type.getUrl() : typeName.toLowerCase();
        Path thumbnailPath = Optional.ofNullable(rootThumbnail)
                .map(p -> FileUtils.resolve(p, typeName)).orElse(null);
        builder.type(typeName, resourceType.maxSize(type.getMaxSize().toBytes())
                .path(FileUtils.resolve(basePath, path))
                .url(PathUtils.normalizeUrl(baseUrl + url))
                .thumbnailPath(thumbnailPath).build());
      }
    }

    @Nullable
    private ThumbnailProperties createThumbs(CKFinderProperties.Thumbs thumbs, BasePathBuilder basePathBuilder) {
      if (thumbs != null && thumbs.isEnabled()) {
        Path basePath = Objects.requireNonNull(basePathBuilder.getBasePath(), "ckfinder base path");
        String baseUrl = Objects.requireNonNull(basePathBuilder.getBaseUrl(), "ckfinder base url");
        String url = PathUtils.normalizeUrl(baseUrl + thumbs.getUrl());
        return ThumbnailProperties.builder()
                .path(FileUtils.resolve(basePath, thumbs.getDirectory()))
                .directAccess(thumbs.isDirectAccess())
                .url(url)
                .maxHeight(thumbs.getMaxHeight())
                .maxWidth(thumbs.getMaxWidth())
                .quality(thumbs.getQuality())
                .build();
      }
      return null;
    }

    private LicenseFactory createLiceFactory(CKFinderProperties.License license) {
      License.Builder licenseBuilder = License.builder()
              .name(license.getName()).key(license.getKey());
      CKFinderProperties.LicenseStrategy strategy = license.getStrategy();
      if (null != strategy) {
        switch (strategy) {
          case HOST:
            return new HostLicenseFactory();
          case AUTH:
            if (StringUtils.hasLength(license.getName())) {
              licenseBuilder.key(KeyGenerator.INSTANCE.generateKey(license.getName(), false));
            }
            break;
          default:
            break;
        }
      }
      return new FixLicenseFactory(licenseBuilder.build());
    }

  }

  @Configuration
  @ConditionalOnMissingBean(FileEditor.class)
  @ConditionalOnProperty(prefix = CKFinderProperties.CKFINDER_PREFIX + ".file-editor", name = "enabled", havingValue = "true", matchIfMissing = true)
  public static class DefaultFileEditorConfigurer {

    @Bean
    public FileEditor fileEditorPlugin() {
      return new FileEditor();
    }

  }

  @Configuration
  @ConditionalOnMissingBean(ImageResize.class)
  @ConditionalOnProperty(prefix = DefaultImageResizeConfigurer.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
  public static class DefaultImageResizeConfigurer {

    static final String PREFIX = CKFinderProperties.CKFINDER_PREFIX + ".image-resize";

    @Bean
    public ImageResize imageResizePlugin(CKFinderProperties properties) {
      CKFinderProperties.ImageResize imageResize = properties.getImageResize();
      Map<ImageResizeParam, ImageResizeSize> params = imageResize.getParams();
      Map<ImageResizeParam, ImageResizeSize> map = new EnumMap<>(ImageResizeParam.class);
      map.putAll(params != null && !params.isEmpty() ? params
              : ImageResizeParam.createDefaultParams());
      return new ImageResize(Collections.unmodifiableMap(map));
    }

  }

  @Configuration
  @ConditionalOnMissingBean(Watermark.class)
  @ConditionalOnProperty(prefix = CKFinderProperties.CKFINDER_PREFIX + ".watermark", name = "enabled", havingValue = "true")
  public static class DefaultWatermarkConfigurer {

    @Bean
    public Watermark watermarkPlugin(CKFinderProperties properties) {
      CKFinderProperties.Watermark watermark = properties.getWatermark();
      WatermarkSettings.Builder builder = WatermarkSettings.builder();
      Resource resource = watermark.getSource();
      Assert.notNull(resource, "watermark source should not be null");
      Assert.isTrue(resource.exists(), "watermark resource not exists");
      return new Watermark(builder.marginBottom(watermark.getMarginBottom())
              .marginRight(watermark.getMarginRight())
              .quality(watermark.getQuality())
              .source(resource)
              .transparency(watermark.getTransparency()).build());
    }
  }

  @Configuration
  @ConditionalOnMissingBean(name = "connectorServlet")
  @ConditionalOnProperty(prefix = CKFinderProperties.CKFINDER_PREFIX + ".servlet", name = "enabled", havingValue = "true", matchIfMissing = true)
  public static class DefaultConnectorServletConfigurer {

    @Bean
    public ServletRegistrationBean<ConnectorServlet> connectorServlet(
            CKFinderProperties properties,
            MultipartConfigElement multipartConfigElement,
            CKFinderContext context) {
      ConnectorServlet servlet = new ConnectorServlet(context);
      ServletRegistrationBean<ConnectorServlet> servletRegistrationBean = new ServletRegistrationBean<>(servlet, false, properties.getServlet().getPath());
      servletRegistrationBean.setMultipartConfig(multipartConfigElement);
      return servletRegistrationBean;
    }

  }

}
