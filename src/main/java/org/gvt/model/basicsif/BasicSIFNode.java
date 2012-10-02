package org.gvt.model.basicsif;

import org.biopax.paxtools.model.level2.Level2Element;
import org.eclipse.draw2d.geometry.Dimension;
import org.gvt.model.CompoundModel;
import org.gvt.model.biopaxl2.BioPAXNode;
import org.gvt.model.biopaxl2.Actor;
import org.patika.mada.util.XRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Ozgun Babur
 *
 * Copyright: Bilkent Center for Bioinformatics, 2007 - present
 */
public class BasicSIFNode extends BioPAXNode
{
	private String rdfid;

	public BasicSIFNode(CompoundModel root, String rdfid, String name)
	{
		super(root);

		this.rdfid = rdfid;

		if (name == null) name = rdfid;
		setText(name);
		addReference(new XRef("name" + XRef.SEPARATOR + name));

		setShape("RoundRect");
		configFromModel();
		
		int width = Math.max(suggestInitialWidth(), Actor.MIN_INITIAL_WIDTH);

		int height = 20;
		setSize(new Dimension(width, height));
	}

	public BasicSIFNode(BioPAXNode excised, CompoundModel root)
	{
		super(excised, root);
		this.rdfid = ((BasicSIFNode) excised).getRdfid();
	}

	public void configFromModel()
	{
		setTooltipText(getText());
		setColor(getStringSpecificColor(getText()));		
	}

	public boolean isEvent()
	{
		return false;
	}

	public Collection<? extends Level2Element> getRelatedModelElements()
	{
		return new ArrayList<Level2Element>();
	}

	public String getRdfid()
	{
		return rdfid;
	}

	public boolean isBreadthNode()
	{
		return true;
	}

	public List<String[]> getInspectable()
	{
		List<String[]> list = super.getInspectable();
		list.add(new String[]{"ID", rdfid});
		return list;
	}
}