/*
 * Copyright 2015-present Facebook, Inc.
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

import com.facebook.buck.apple.AppleBundleResources;
import com.facebook.buck.apple.HasAppleBundleResourcesDescription;
import com.facebook.buck.io.ProjectFilesystem;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.Flavor;
import com.facebook.buck.model.Flavored;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.BuildTargetSourcePath;
import com.facebook.buck.rules.CellPathResolver;
import com.facebook.buck.rules.Description;
import com.facebook.buck.rules.ExplicitBuildTargetSourcePath;
import com.facebook.buck.rules.ImplicitDepsInferringDescription;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.TargetGraph;
import com.facebook.buck.rules.TargetNode;
import com.facebook.buck.util.RichStream;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

public class IosReactNativeLibraryDescription
    implements Description<ReactNativeLibraryArg>,
        Flavored,
        HasAppleBundleResourcesDescription<ReactNativeLibraryArg>,
        ImplicitDepsInferringDescription<ReactNativeLibraryArg> {

  private final ReactNativeLibraryGraphEnhancer enhancer;
  private final Supplier<SourcePath> packager;

  public IosReactNativeLibraryDescription(final ReactNativeBuckConfig buckConfig) {
    this.enhancer = new ReactNativeLibraryGraphEnhancer(buckConfig);
    this.packager = buckConfig::getPackagerSourcePath;
  }

  @Override
  public Class<ReactNativeLibraryArg> getConstructorArgType() {
    return ReactNativeLibraryArg.class;
  }

  @Override
  public ReactNativeBundle createBuildRule(
      TargetGraph targetGraph,
      BuildRuleParams params,
      BuildRuleResolver resolver,
      CellPathResolver cellRoots,
      ReactNativeLibraryArg args) {
    return enhancer.enhanceForIos(params, resolver, args);
  }

  @Override
  public boolean hasFlavors(ImmutableSet<Flavor> flavors) {
    return ReactNativeFlavors.validateFlavors(flavors);
  }

  @Override
  public void findDepsForTargetFromConstructorArgs(
      BuildTarget buildTarget,
      CellPathResolver cellRoots,
      ReactNativeLibraryArg constructorArg,
      ImmutableCollection.Builder<BuildTarget> extraDepsBuilder,
      ImmutableCollection.Builder<BuildTarget> targetGraphOnlyDepsBuilder) {
    RichStream.of(packager.get())
        .filter(BuildTargetSourcePath.class)
        .map(BuildTargetSourcePath::getTarget)
        .forEach(extraDepsBuilder::add);
  }

  @Override
  public void addAppleBundleResources(
      AppleBundleResources.Builder builder,
      TargetNode<ReactNativeLibraryArg, ?> targetNode,
      ProjectFilesystem filesystem,
      BuildRuleResolver resolver) {
    BuildTarget buildTarget = targetNode.getBuildTarget();
    builder.addDirsContainingResourceDirs(
        new ExplicitBuildTargetSourcePath(
            buildTarget, ReactNativeBundle.getPathToJSBundleDir(buildTarget, filesystem)),
        new ExplicitBuildTargetSourcePath(
            buildTarget, ReactNativeBundle.getPathToResources(buildTarget, filesystem)));
  }
}
