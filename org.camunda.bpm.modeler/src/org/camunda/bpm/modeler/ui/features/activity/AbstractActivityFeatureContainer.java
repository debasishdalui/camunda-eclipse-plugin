/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Innar Made
 ******************************************************************************/
package org.camunda.bpm.modeler.ui.features.activity;

import org.camunda.bpm.modeler.core.features.MultiUpdateFeature;
import org.camunda.bpm.modeler.core.features.UpdateDecorationFeature;
import org.camunda.bpm.modeler.core.features.activity.ActivityDecorateFeature;
import org.camunda.bpm.modeler.core.features.activity.MoveActivityFeature;
import org.camunda.bpm.modeler.core.features.activity.UpdateActivityCompensateMarkerFeature;
import org.camunda.bpm.modeler.core.features.activity.UpdateActivityLoopAndMultiInstanceMarkerFeature;
import org.camunda.bpm.modeler.core.features.api.IDecorateFeature;
import org.camunda.bpm.modeler.core.features.container.BaseElementFeatureContainer;
import org.camunda.bpm.modeler.core.features.event.AbstractBoundaryEventOperation;
import org.camunda.bpm.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.camunda.bpm.modeler.ui.features.event.AppendEventFeature;
import org.camunda.bpm.modeler.ui.features.gateway.AppendGatewayFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

public abstract class AbstractActivityFeatureContainer extends BaseElementFeatureContainer {

	@Override
	public MultiUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		
		UpdateActivityCompensateMarkerFeature compensateMarkerUpdateFeature = new UpdateActivityCompensateMarkerFeature(fp);
		UpdateActivityLoopAndMultiInstanceMarkerFeature loopAndMultiInstanceUpdateFeature = new UpdateActivityLoopAndMultiInstanceMarkerFeature(fp);
		
		return new MultiUpdateFeature(fp)
			.addUpdateFeature(compensateMarkerUpdateFeature)
			.addUpdateFeature(loopAndMultiInstanceUpdateFeature)
			.addUpdateFeature(new UpdateDecorationFeature(fp));
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new ResizeActivityFeature(fp);
	}

	@Override
	public IDecorateFeature getDecorateFeature(IFeatureProvider fp) {
		return new ActivityDecorateFeature(fp);
	}
	
	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveActivityFeature(fp);
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new AbstractDefaultDeleteFeature(fp) {
			@Override
			public void delete(final IDeleteContext context) {
				Shape shape = (Shape) context.getPictogramElement();
				
				new AbstractBoundaryEventOperation() {
					@Override
					protected void applyTo(ContainerShape container) {
						IDeleteContext delete = new DeleteContext(container);
						getFeatureProvider().getDeleteFeature(delete).delete(delete);
					}
				}.execute(shape);
				
				super.delete(context);
			}
		};
	}

	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[4 + superFeatures.length];
		int i;
		for (i=0; i<superFeatures.length; ++i)
			thisFeatures[i] = superFeatures[i];
		thisFeatures[i++] = new AppendActivityFeature(fp);
		thisFeatures[i++] = new AppendGatewayFeature(fp);
		thisFeatures[i++] = new AppendEventFeature(fp);
		thisFeatures[i++] = new MorphActivityFeature(fp);
		return thisFeatures;
	}

}