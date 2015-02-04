/*-
 * Copyright 2014 The American National Corpus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anc.lapps.stanford;

import org.anc.io.UTF8Reader;
import org.anc.resource.ResourceLoader;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.*;
import org.lappsgrid.discriminator.Constants;
import org.lappsgrid.experimental.annotations.CommonMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Error;
import org.lappsgrid.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Keith Suderman
 */
@CommonMetadata(
	vendor = "http://www.anc.org",
	license = "apache2",
	format = "lapps",
	language = "en"
)
public abstract class AbstractStanfordService implements WebService
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractStanfordService.class);

   protected String metadata;

   public AbstractStanfordService(Class<?> serviceClass)
   {
      try
      {
         loadMetadata(serviceClass);
      }
      catch (IOException ignored)
      {
         // The only IOException not handled by loadMetadata is the one
         // thrown when closing the input stream, and by that point we
         // are good to go so we ignore it.
      }
   }

   protected boolean isError(String discriminator)
   {
      return Constants.Uri.ERROR.equals(discriminator);
   }

   protected String createError(String message)
   {
      return Serializer.toPrettyJson(new Error(message));
   }

   private void loadMetadata(Class<?> serviceClass) throws IOException
   {
      ClassLoader loader = ResourceLoader.getClassLoader();
      String resourceName = "metadata/" + serviceClass.getName() + ".json";
      InputStream inputStream = loader.getResourceAsStream(resourceName);
      if (inputStream == null)
      {
			String message = "Unable to load resource " + resourceName;
			logger.error(message);
         throw new IOException(message);
      }

      UTF8Reader reader = null;
      try
      {
         reader = new UTF8Reader(inputStream);
         String content = reader.readString();
         Data<String> data = new Data<>(Constants.Uri.META, content);
         metadata = data.asJson();
			logger.info("Loaded metadata.");
//         metadata = DataFactory.meta(json);
      }
      catch (IOException e)
      {
//         metadata = DataFactory.error("Unable to load metadata from " + resourceName, e);
			String message = "Unable to load metadata from " + resourceName;
         metadata = Serializer.toPrettyJson(new Error(message));
         throw e;
      }
      finally
      {
         if (reader != null)
         {
            reader.close();
         }
      }
   }


   public String getMetadata()
   {
      return metadata;
   }
}
