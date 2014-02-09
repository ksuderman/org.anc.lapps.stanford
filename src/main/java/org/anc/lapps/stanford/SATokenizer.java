package org.anc.lapps.stanford;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.anc.lapps.serialization.Container;
import org.anc.lapps.serialization.ProcessingStep;
import org.anc.lapps.stanford.util.Converter;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class SATokenizer implements WebService
{
   private static final Logger logger = LoggerFactory.getLogger(SATokenizer.class);

   @Override
   public Data execute(Data input)
   {
      logger.info("Executing Stanford stand-alone tokenizer");
      Container container = createContainer(input);
      if (container == null)
      {
         return input;
      }
      Data data = null;
      String text = container.getText();
      
      List<CoreLabel> tokens = new ArrayList<CoreLabel>();
      PTBTokenizer ptbt = new PTBTokenizer(new StringReader(text), new CoreLabelTokenFactory(), "");
      for (CoreLabel label; ptbt.hasNext(); )
      {
         label = (CoreLabel) ptbt.next();
         tokens.add(label);
      }
      if (tokens.size() == 0)
      {
         return DataFactory.error("PTBTokenizer returned no tokens.");
      }
      
      ProcessingStep step = Converter.addTokens(new ProcessingStep(), tokens);
      step.getMetadata().put(Metadata.PRODUCED_BY, "Stanford Standalone PTBTokenizer");
      container.getSteps().add(step);
      data = DataFactory.json(container.toJson());
      
      return data;
   }
   
   protected Container createContainer(Data input)
   {
      Container container = null;
      long inputType = input.getDiscriminator();
      if (inputType == Types.ERROR)
      {
         return null;
      }
      else if (inputType == Types.TEXT)
      {
         container = new Container();
         container.setText(input.getPayload());
      }
      else if (inputType == Types.JSON)
      {
         container = new Container(input.getPayload());
      }
      return container;
   }

   @Override
   public long[] requires()
   {
      return new long[]{Types.STANFORD, Types.SENTENCE};
   }

   @Override
   public long[] produces()
   {
      return new long[]{Types.STANFORD, Types.SENTENCE, Types.TOKEN};
   }

   @Override
   public Data configure(Data arg0)
   {
      return DataFactory.error("Unsupported operation.");
   }
}
