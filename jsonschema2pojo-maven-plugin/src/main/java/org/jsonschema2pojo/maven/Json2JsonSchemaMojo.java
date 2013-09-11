/**
 * Copyright © 2010-2013 Nokia
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

package org.jsonschema2pojo.maven;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jsonschema2pojo.SchemaGenerator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 **
 * When invoked, this goal reads one or more <a
 * href="http://json-schema.org/">JSON Files</a> documents and generates JSON
 * Schema
 *
 * @goal generate-schema
 * @phase generate-resources
 * @threadSafe
 * @see <a
 *      href="http://maven.apache.org/developers/mojo-api-specification.html">Mojo
 *      API Specification</a>
 * User: justin.spears
 * Date: 9/11/13
 * Time: 12:50 PM
 */

public class Json2JsonSchemaMojo extends AbstractMojo{
    /**
     * Target directory for generated Java source files.
     *
     * @parameter expression="${jsonschema2pojo.outputDirectory}"
     *            default-value="${project.basedir}/src/main/resources"
     * @since 0.3.8
     */
    private File outputDirectory;

    /**
     * Location of the JSON Schema file(s). Note: this may refer to a single
     * file or a directory of files.
     *
     * @parameter expression="${jsonschema2pojo.sourceDirectory}"
     * @since 0.3.8
     */
    private File sourceDirectory;

    /**
     * An array of locations of the JSON Schema file(s). Note: each item may
     * refer to a single file or a directory of files.
     *
     * @parameter expression="${jsonschema2pojo.sourcePaths}"
     * @since 0.3.8
     */
    private File[] sourcePaths;

    /**
     * The project being built.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (null == sourceDirectory && null == sourcePaths) {
            throw new MojoExecutionException("One of sourceDirectory or sourcePaths must be provided");
        }
        ObjectMapper mapper = new ObjectMapper();
        SchemaGenerator sg = new SchemaGenerator();
        for(File f : getSource()){
            generateSchema(f, mapper,sg);
        }
    }
    protected void generateSchema(File child, ObjectMapper mapper, SchemaGenerator sg) throws MojoExecutionException{
        if (child.isDirectory()) {
            for(File f : child.listFiles()){
                generateSchema(f, mapper, sg);
            }

        }else if (child.isFile() && child.canRead()) {
            ObjectNode schema = null;
            System.out.println(" generating for "+child.getAbsolutePath());
            try {
                schema = sg.schemaFromExample(child.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Could not create", e);
            }
            try {
                File out = new File(getTargetDirectory(), child.getName().replace(".json", "")+"-schema.json");
                mapper.writerWithDefaultPrettyPrinter().writeValue(out, schema);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create", e);
            }
        }
    }
    public File getTargetDirectory() {
        return outputDirectory;
    }

    public Iterable<File> getSource() {
        if (null != sourceDirectory) {
            return Collections.singleton(sourceDirectory);
        }
        return Arrays.asList(sourcePaths);
    }
}
