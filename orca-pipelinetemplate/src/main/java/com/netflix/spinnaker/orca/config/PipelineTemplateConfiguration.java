/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.netflix.spinnaker.orca.front50.Front50Service;
import com.netflix.spinnaker.orca.front50.PipelineModelMutator;
import com.netflix.spinnaker.orca.pipelinetemplate.PipelineTemplateModule;
import com.netflix.spinnaker.orca.pipelinetemplate.loader.TemplateLoader;
import com.netflix.spinnaker.orca.pipelinetemplate.v1schema.TemplatedPipelineModelMutator;
import com.netflix.spinnaker.orca.pipelinetemplate.v1schema.render.JinjaRenderer;
import com.netflix.spinnaker.orca.pipelinetemplate.v1schema.render.RenderedValueConverter;
import com.netflix.spinnaker.orca.pipelinetemplate.v1schema.render.Renderer;
import com.netflix.spinnaker.orca.pipelinetemplate.v1schema.render.YamlRenderedValueConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.yaml.snakeyaml.Yaml;

@ConditionalOnProperty("pipelineTemplate.enabled")
@ComponentScan(
  basePackageClasses = PipelineTemplateModule.class,
  basePackages = {"com.netflix.spinnaker.orca.pipelinetemplate.tasks", "com.netflix.spinnaker.orca.pipelinetemplate.pipeline"}
)
public class PipelineTemplateConfiguration {

  @Bean
  ObjectMapper pipelineTemplateObjectMapper() {
    return new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  @Bean
  RenderedValueConverter yamlRenderedValueConverter() {
    return new YamlRenderedValueConverter(new Yaml());
  }

  @Bean
  Renderer jinjaRenderer(RenderedValueConverter renderedValueConverter, ObjectMapper pipelineTemplateObjectMapper, Front50Service front50Service) {
    return new JinjaRenderer(renderedValueConverter, pipelineTemplateObjectMapper, front50Service);
  }

  @Bean
  PipelineModelMutator schemaV1TemplatedPipelineModelMutator(ObjectMapper pipelineTemplateObjectMapper,
                                                             TemplateLoader templateLoader,
                                                             Renderer renderer) {
    return new TemplatedPipelineModelMutator(pipelineTemplateObjectMapper, templateLoader, renderer);
  }
}
