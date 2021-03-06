/*
 * Copyright 2017-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.js;

import com.facebook.buck.io.ProjectFilesystem;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.Either;
import com.facebook.buck.model.Pair;
import com.facebook.buck.rules.AbstractNodeBuilderWithMutableArg;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.test.selectors.Nullable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Optional;

public class JsLibraryBuilder
    extends AbstractNodeBuilderWithMutableArg<
        JsLibraryDescription.Arg, JsLibraryDescription, JsLibrary> {
  private static final JsLibraryDescription libraryDescription = new JsLibraryDescription();

  JsLibraryBuilder(BuildTarget target, BuildTarget worker, ProjectFilesystem filesystem) {
    super(libraryDescription, target, filesystem);
    arg.extraArgs = Optional.empty();
    arg.worker = worker;
    arg.srcs = ImmutableSortedSet.of();
    arg.basePath = Optional.empty();
  }

  JsLibraryBuilder setLibs(ImmutableSortedSet<BuildTarget> libs) {
    arg.libs = libs;
    return this;
  }

  JsLibraryBuilder setExtraArgs(String extraArgs) {
    arg.extraArgs = Optional.of(extraArgs);
    return this;
  }

  JsLibraryBuilder setSrcs(ImmutableSet<Either<SourcePath, Pair<SourcePath, String>>> srcs) {
    arg.srcs = srcs;
    return this;
  }

  JsLibraryBuilder setBasePath(@Nullable String basePath) {
    arg.basePath = Optional.ofNullable(basePath);
    return this;
  }
}
