package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.introduce.portal.common.jedit.DefaultInputHandler;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.SyntaxDocument;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.SyntaxStyle;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.TextAreaDefaults;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.Token;

import java.awt.Color;

public class GMETextAreaDefaults
{

	public static TextAreaDefaults createDefaults()
	{
	 TextAreaDefaults DEFAULTS;
			DEFAULTS = new TextAreaDefaults();

			DEFAULTS.inputHandler = new DefaultInputHandler();
			DEFAULTS.inputHandler.addDefaultKeyBindings();
			DEFAULTS.document = new SyntaxDocument();
			DEFAULTS.editable = false;

			DEFAULTS.caretVisible = true;
			DEFAULTS.caretBlinks = false;
			DEFAULTS.electricScroll = 0;

			DEFAULTS.cols = 25;
			DEFAULTS.rows = 10;
			DEFAULTS.styles = GMETextAreaDefaults.getDefaultSyntaxStyles();
			DEFAULTS.caretColor = Color.red;
			DEFAULTS.selectionColor = new Color(0xccccff);
			DEFAULTS.lineHighlightColor = new Color(0xe0e0e0);
			DEFAULTS.lineHighlight = true;
			DEFAULTS.bracketHighlightColor = Color.black;
			DEFAULTS.bracketHighlight = true;
			DEFAULTS.eolMarkerColor = new Color(0x009999);
			DEFAULTS.eolMarkers = false;
			DEFAULTS.paintInvalid = true;
		return DEFAULTS;
	}
	
	public static SyntaxStyle[] getDefaultSyntaxStyles()
	{
		SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];

		styles[Token.COMMENT1] = new SyntaxStyle(Color.black,true,false);
		styles[Token.COMMENT2] = new SyntaxStyle(new Color(0x990033),true,false);
		styles[Token.KEYWORD1] = new SyntaxStyle(Color.blue,false,true);
		styles[Token.KEYWORD2] = new SyntaxStyle(Color.orange,false,false);
		styles[Token.KEYWORD3] = new SyntaxStyle(new Color(0x009600),false,false);
		styles[Token.LITERAL1] = new SyntaxStyle(new Color(0x650099),false,false);
		styles[Token.LITERAL2] = new SyntaxStyle(new Color(0x650099),false,true);
		styles[Token.LABEL] = new SyntaxStyle(new Color(0x990033),false,true);
		styles[Token.OPERATOR] = new SyntaxStyle(Color.orange,false,false);
		styles[Token.INVALID] = new SyntaxStyle(Color.red,false,true);

		return styles;
	}
}
