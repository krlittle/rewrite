/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.maven.search;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.marker.RecipeSearchResult;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.maven.tree.Maven;
import org.openrewrite.xml.tree.Xml;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Value
public class FindDependency extends Recipe {
    String groupId;
    String artifactId;

    public static Set<Xml.Tag> find(Maven maven, String groupId, String artifactId) {
        Set<Xml.Tag> ds = new HashSet<>();
        new MavenVisitor() {
            @Override
            public Xml visitTag(Xml.Tag tag, ExecutionContext context) {
                if (isDependencyTag(groupId, artifactId)) {
                    ds.add(tag);
                }
                return super.visitTag(tag, context);
            }
        }.visit(maven, new InMemoryExecutionContext());
        return ds;
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenVisitor() {
            @Override
            public Xml visitTag(Xml.Tag tag, ExecutionContext context) {
                if (isDependencyTag(groupId, artifactId)) {
                    return tag.withMarker(new RecipeSearchResult(FindDependency.this));
                }
                return super.visitTag(tag, context);
            }
        };
    }
}
