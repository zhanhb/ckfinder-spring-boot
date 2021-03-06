/*
 * Copyright 2017 zhanhb.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zhanhb.ckfinder.download;

import javax.annotation.Nullable;

/**
 *
 * @author zhanhb
 */
public interface ContentTypeResolver extends Strategy<String> {

  static ContentTypeResolver getDefault() {
    return context -> context.getServletContext().getMimeType(context.getPath().getFileName().toString());
  }

  @Nullable
  @Override
  String apply(PartialContext context);

}
