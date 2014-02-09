package org.anc.lapps.stanford.util;

import java.util.ArrayList;
import java.util.List;

import org.anc.lapps.serialization.Annotation;
import org.anc.lapps.serialization.Container;
import org.anc.lapps.serialization.ProcessingStep;
import org.anc.lapps.stanford.LappsCoreLabel;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreLabel;

public class StanfordUtils
{
   private static final Logger logger = LoggerFactory.getLogger(StanfordUtils.class);
   
   public static List<CoreLabel> getListOfTaggedCoreLabels(Container container)
   {
      List<ProcessingStep> steps = container.getSteps();
      ProcessingStep taggedStep = null;
      for (int i = steps.size() - 1; i >= 0; i--)
      {
         ProcessingStep step = container.getSteps().get(i);
         boolean hasTags = false;
         String contains = (String) step.getMetadata().get("contains");
         if (contains != null)
         {
            hasTags = contains.contains("POS");
         }
         else
         {
            String producedBy = (String) step.getMetadata().get(Metadata.PRODUCED_BY);
            hasTags = producedBy.toLowerCase().contains("tagger");
         }
         
         if (hasTags)
         {
            taggedStep = step;
            break;
         }
      }
      
      if (taggedStep == null)
      {
         return null; 
      }
      
      List<Annotation> annotations = taggedStep.getAnnotations();
      List<CoreLabel> labels = new ArrayList<CoreLabel>();
      for (Annotation a : annotations)
      {
         labels.add(new LappsCoreLabel(a));
      }
      return labels;
   }
}
