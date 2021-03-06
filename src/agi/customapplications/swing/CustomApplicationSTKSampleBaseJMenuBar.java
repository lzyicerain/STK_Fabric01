/* ==============================================================
 * This sample was last tested with the following configuration:
 * ==============================================================
 * Eclipse 3.7.0 Build id: I20110613-1736
 * JRE 1.4.2_10 and greater
 * STK 10.0
 * ==============================================================
 */
package agi.customapplications.swing;

//Java

import agi.stk.core.swing.menus.scenario.AgScenarioJMenu;
import agi.stk.core.swing.menus.scenario.IAgScenarioJMenuEventsListener;
import agi.stk.core.swing.menus.tools.AgToolsJMenu;
import agi.stk.core.swing.menus.tools.IAgToolsJMenuEventsListener;
import agi.stk.core.swing.menus.vdf.AgVDFJMenu;
import agi.stk.core.swing.menus.vdf.IAgVDFJMenuEventsListener;
import agi.swing.menus.help.AgHelpJMenu;
import agi.swing.menus.help.IAgHelpJMenuEventsListener;
import agi.swing.menus.java.AgJavaJMenu;
import agi.swing.menus.java.IAgJavaJMenuEventsListener;

import javax.swing.*;

public class CustomApplicationSTKSampleBaseJMenuBar
extends JMenuBar
{
	private static final long	serialVersionUID	= 1L;

	public CustomApplicationSTKSampleJMenu	m_CustomApplicationSTKSampleJMenu;
	public AgScenarioJMenu					m_AgScenarioJMenu;
	public AgVDFJMenu						m_AgVDFJMenu;
	public AgToolsJMenu						m_AgToolsJMenu;
	public AgJavaJMenu						m_AgJavaJMenu;
	public AgHelpJMenu						m_AgHelpJMenu;

	public CustomApplicationSTKSampleBaseJMenuBar()
	{
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		this.m_AgToolsJMenu = new AgToolsJMenu();
		this.m_AgToolsJMenu.remove(this.m_AgToolsJMenu.getMsgViewerJMenuItem());
		this.add(this.m_AgToolsJMenu);

		this.m_AgJavaJMenu = new AgJavaJMenu();
		this.add(this.m_AgJavaJMenu);

		this.m_AgHelpJMenu = new AgHelpJMenu();
		this.add(this.m_AgHelpJMenu);

		this.m_AgScenarioJMenu = new AgScenarioJMenu();
		this.add(this.m_AgScenarioJMenu);

		this.m_AgVDFJMenu = new AgVDFJMenu();
		this.add(this.m_AgVDFJMenu);

		this.m_CustomApplicationSTKSampleJMenu = new CustomApplicationSTKSampleJMenu();
		this.add(this.m_CustomApplicationSTKSampleJMenu);
	}

	public CustomApplicationSTKSampleJMenu getSampleJMenu()
	{
		return this.m_CustomApplicationSTKSampleJMenu;
	}

	public AgScenarioJMenu getScenarioJMenu()
	{
		return this.m_AgScenarioJMenu;
	}

	public AgVDFJMenu getVDFJMenu()
	{
		return this.m_AgVDFJMenu;
	}

	public AgToolsJMenu getToolsJMenu()
	{
		return this.m_AgToolsJMenu;
	}

	public AgJavaJMenu getJavaJMenu()
	{
		return this.m_AgJavaJMenu;
	}

	public AgHelpJMenu getHelpJMenu()
	{
		return this.m_AgHelpJMenu;
	}

	public void addScenarioJMenuListener(IAgScenarioJMenuEventsListener l)
	{
		this.m_AgScenarioJMenu.addScenarioJMenuListener(l);
	}

	public void removeScenarioJMenuListener(IAgScenarioJMenuEventsListener l)
	{
		this.m_AgScenarioJMenu.removeScenarioJMenuListener(l);
	}

	public void addVDFJMenuListener(IAgVDFJMenuEventsListener l)
	{
		this.m_AgVDFJMenu.addVDFJMenuListener(l);
	}

	public void removeVDFJMenuListener(IAgVDFJMenuEventsListener l)
	{
		this.m_AgVDFJMenu.removeVDFJMenuListener(l);
	}

	public void addToolsJMenuListener(IAgToolsJMenuEventsListener l)
	{
		this.m_AgToolsJMenu.addToolsJMenuListener(l);
	}

	public void removeToolsJMenuListener(IAgToolsJMenuEventsListener l)
	{
		this.m_AgToolsJMenu.removeToolsJMenuListener(l);
	}

	public void addJavaJMenuListener(IAgJavaJMenuEventsListener l)
	{
		this.m_AgJavaJMenu.addJavaJMenuListener(l);
	}

	public void removeJavaJMenuListener(IAgJavaJMenuEventsListener l)
	{
		this.m_AgJavaJMenu.removeJavaJMenuListener(l);
	}

	public void addHelpJMenuListener(IAgHelpJMenuEventsListener l)
	{
		this.m_AgHelpJMenu.addHelpJMenuListener(l);
	}

	public void removeHelpJMenuListener(IAgHelpJMenuEventsListener l)
	{
		this.m_AgHelpJMenu.removeHelpJMenuListener(l);
	}
}