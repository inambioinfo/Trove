package dialogFrames;

//Imports are listed in full to show what's being used could just import javax.swing.* and java.awt.* etc..
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gephi.preview.api.ManagedRenderer;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import constants.stringConstant;
import cz.cvut.fit.krizeji1.multicolour.MultiColourRenderer;

import database.postgreSQL;
import dialogFrames.viewSelectedHallmarkSummaryDialog.VisualizeNetworkWorker;

import graph.Gephi_graph;
import hallmark.allHallmark;

@SuppressWarnings("serial")

public class viewNeighbourDialog extends JDialog{
	private JButton visualizeButton;
	private JList<String> nodeJList;
	private JScrollPane nodeScrollPane;
	private JPanel graphPanel, hallmarkLegendContent = new JPanel();
	private JScrollPane vertexLegendScroll = new JScrollPane(hallmarkLegendContent);
	private JCheckBox proliferationCheckBox, growthRepressorCheckBox, apoptosisCheckBox, replicativeImmortalityCheckBox;
	private JCheckBox angiogenesisCheckBox, metastasisCheckBox, metabolismCheckBox, immuneDestructionCheckBox;
	private JCheckBox genomeInstabilityCheckBox, tumorPromotingInflammationCheckBox;
	
	protected final static String VISUALIZE = "Visualize";
	
	int selectedIndex;
	
	private stringConstant strConstants=new stringConstant();
	
	private String HALLMARK_PROLIFERATION_GUI = strConstants.getGUIProliferation();
	private String HALLMARK_GROWTH_REPRESSOR_GUI = strConstants.getGUIGrowthRepressor();
	private String HALLMARK_APOPTOSIS_GUI = strConstants.getGUIApoptosis();
	private String HALLMARK_REPLICATIVE_IMMORTALITY_GUI = strConstants.getGUIReplicativeImmortality();
	private String HALLMARK_ANGIOGENESIS_GUI = strConstants.getGUIAngiogenesis();
	private String HALLMARK_METASTASIS_GUI = strConstants.getGUIMetastasis();
	private String HALLMARK_METABOLISM_GUI = strConstants.getGUIMetabolism();
	private String HALLMARK_IMMUNE_DESTRUCTION_GUI = strConstants.getGUIImmuneDestruction();
	private String HALLMARK_GENOME_INSTABILITY_GUI = strConstants.getGUIGenomeInstability();
	private String HALLMARK_TUMOR_PROMOTING_INFLAMMATION_GUI = strConstants.getGUITumorPromotingInflammation();
	
	private String HALLMARK_PROLIFERATION_DB;
	private String HALLMARK_GROWTH_REPRESSOR_DB;
	private String HALLMARK_APOPTOSIS_DB;
	private String HALLMARK_REPLICATIVE_IMMORTALITY_DB;
	private String HALLMARK_ANGIOGENESIS_DB;
	private String HALLMARK_METASTASIS_DB;
	private String HALLMARK_METABOLISM_DB;
	private String HALLMARK_IMMUNE_DESTRUCTION_DB;
	private String HALLMARK_GENOME_INSTABILITY_DB;
	private String HALLMARK_TUMOR_PROMOTING_INFLAMMATION_DB;

	private String node;
	private ArrayList<String> neighbourList;
	private ArrayList<String> nodeList;
	private ArrayList<ArrayList<String>> inducedEdges;
	private postgreSQL postgreDB;
	private Gephi_graph gGraph;
	
	private ProjectController interactivePC;
	private Project interactiveProject;
	private Workspace previousWorkspace, currWorkspace;
	private PreviewController interactivePreviewController;
	private ProcessingTarget interactiveTarget;
	private PreviewModel interactivePreviewModel;
	private MultiColourRenderer multiColorRenderer=new MultiColourRenderer();
	
	private ArrayList<String> entireNodeList;
	private ArrayList<String> selectedHallmarks=new ArrayList<String>();
	private ArrayList<String> entireHallmarkList=new ArrayList<String>();
	
	private int osType;
	//threading
	VisualizeNetworkWorker visualizeNetworkWorker;
		
	public viewNeighbourDialog(ProjectController pc, Project project, Workspace oldWorkspace, Workspace newWorkspace, String n, 
			ArrayList<String> allNodeArrList, postgreSQL dB, ArrayList<String> hallmarkSelected, int sysOS)
	{
		//initialize variables
		node=n;
		entireNodeList=allNodeArrList;
		postgreDB=dB;
		selectedHallmarks=hallmarkSelected;
		osType=sysOS;
		
		HALLMARK_PROLIFERATION_DB=strConstants.getDBProliferation();
		HALLMARK_GROWTH_REPRESSOR_DB=strConstants.getDBGrowthRepressor();
		HALLMARK_APOPTOSIS_DB=strConstants.getDBApoptosis();
		HALLMARK_REPLICATIVE_IMMORTALITY_DB=strConstants.getDBReplicativeImmortality();
		HALLMARK_ANGIOGENESIS_DB=strConstants.getDBAngiogenesis();
		HALLMARK_METASTASIS_DB=strConstants.getDBMetastasis();
		HALLMARK_METABOLISM_DB=strConstants.getDBMetabolism();
		HALLMARK_IMMUNE_DESTRUCTION_DB=strConstants.getDBImmuneDestruction();
		HALLMARK_GENOME_INSTABILITY_DB=strConstants.getDBGenomeInstability();
		HALLMARK_TUMOR_PROMOTING_INFLAMMATION_DB=strConstants.getDBTumorPromotingInflammation();
		
		entireHallmarkList.add(HALLMARK_PROLIFERATION_DB);
		entireHallmarkList.add(HALLMARK_GROWTH_REPRESSOR_DB);
		entireHallmarkList.add(HALLMARK_APOPTOSIS_DB);
		entireHallmarkList.add(HALLMARK_REPLICATIVE_IMMORTALITY_DB);
		entireHallmarkList.add(HALLMARK_ANGIOGENESIS_DB);
		entireHallmarkList.add(HALLMARK_METASTASIS_DB);
		entireHallmarkList.add(HALLMARK_METABOLISM_DB);
		entireHallmarkList.add(HALLMARK_IMMUNE_DESTRUCTION_DB);
		entireHallmarkList.add(HALLMARK_GENOME_INSTABILITY_DB);
		entireHallmarkList.add(HALLMARK_TUMOR_PROMOTING_INFLAMMATION_DB);
		
		interactivePC=pc;
		interactiveProject=project;
		previousWorkspace=oldWorkspace;
		currWorkspace=newWorkspace;
		interactivePC.openWorkspace(currWorkspace); 
		//interactivePreviewController=Lookup.getDefault().lookup(PreviewController.class);
		//interactivePreviewModel = interactivePreviewController.getModel(interactiveWorkspace);
		//interactiveTarget = (ProcessingTarget) interactivePreviewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
		  
		interactivePreviewController = Lookup.getDefault().lookup(PreviewController.class);
		interactivePreviewModel=interactivePreviewController.getModel(interactivePC.getCurrentWorkspace());
		ManagedRenderer [] mr = interactivePreviewModel.getManagedRenderers();
		ManagedRenderer [] mr2 = Arrays.copyOf(mr, mr.length+1);
		mr2[mr.length] = new ManagedRenderer(multiColorRenderer, true);
		interactivePreviewModel.setManagedRenderers(mr2);
		  
		//interactivePreviewController=Lookup.getDefault().lookup(PreviewController.class);
		//interactivePreviewModel = interactivePreviewController.getModel(interactiveWorkspace);
		//interactiveTarget = (ProcessingTarget) interactivePreviewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
		  
		
		//gGraph=new Gephi_graph(interactivePC, interactiveProject, currWorkspace); //for visualization
		
		//gGraph=new Gephi_graph();
		nodeList=new ArrayList<String>();
		nodeList.add(node);
		neighbourList=postgreDB.getNetworkEdge_neighbourNameOfNode(node);
		neighbourList.retainAll(allNodeArrList);
		for(int i=0; i<neighbourList.size(); i++)
			nodeList.add(neighbourList.get(i));
		inducedEdges=postgreDB.getNetworkEdge_inducedEdges(nodeList);

		//set JDialog properties
		setTitle("Neighbour(s) of "+n);
		setSize(950,700);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);

		//configure components in JDialog
		initializeComponents();
		
		gGraph=new Gephi_graph(interactivePC, interactiveProject, currWorkspace, interactivePreviewModel); //for visualization
		createGraph();
		gGraph.configureInteractivePreview();
		gGraph.visualizeGraph(graphPanel);
		gGraph.colorAndResizeSelectedNode(node, true);
	}

	private String suggestGephiNodeSizeToUse(int size) 
	{
		if(size<10)
			return strConstants.getGephiExtraLarge();
		if(size<50)
			return strConstants.getGephiLarge();
		else if(size<500)
			return strConstants.getGephiMedium();
		else
			return strConstants.getGephiSmall();
	}

	private void createGraph() 
	{
		int i=0;
		String name;
		String nodeSize=suggestGephiNodeSizeToUse(inducedEdges.size());

		gGraph.destroyGraph();

		System.out.println("graphNodeIDList size:"+nodeList.size());
		System.out.println("graphEdgeList size:"+inducedEdges.size());

		//add all vertices
		for(i=0; i<nodeList.size(); i++)
		{
			name=nodeList.get(i);
			gGraph.addANode(name, nodeSize);
		}

		//add all edges
		for(i=0; i<inducedEdges.size(); i++)
		{
			ArrayList<String> inducedEdge=inducedEdges.get(i);
			String sourceNodeName=inducedEdge.get(0);
			String targetNodeName=inducedEdge.get(1);
			String type=inducedEdge.get(2);

			System.out.println(sourceNodeName+" "+targetNodeName+" "+type);
			if(type.compareTo("Phy")==0)
				gGraph.addAnUndirectedEdge(sourceNodeName, targetNodeName);
			else
				gGraph.addADirectedEdge(sourceNodeName, targetNodeName);
		}
		gGraph.updateEdgeThickness(nodeSize);
	}

	private ActionListener actionListener = new ActionListener() 
	{
		public void actionPerformed(ActionEvent act) {
			if (act.getActionCommand() == VISUALIZE)
				visualize_actionPerformed();
		}
	};

	private void visualize_actionPerformed() 
	{
		ArrayList<String> reapplySelectedHallmark=new ArrayList<String>();
		if(proliferationCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_PROLIFERATION_DB);
		if(growthRepressorCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_GROWTH_REPRESSOR_DB);
		if(apoptosisCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_APOPTOSIS_DB);
		if(replicativeImmortalityCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_REPLICATIVE_IMMORTALITY_DB);
		if(angiogenesisCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_ANGIOGENESIS_DB);
		if(metastasisCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_METASTASIS_DB);
		if(metabolismCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_METABOLISM_DB);
		if(immuneDestructionCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_IMMUNE_DESTRUCTION_DB);
		if(genomeInstabilityCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_GENOME_INSTABILITY_DB);
		if(tumorPromotingInflammationCheckBox.isSelected()==true)
			reapplySelectedHallmark.add(HALLMARK_TUMOR_PROMOTING_INFLAMMATION_DB);
		allHallmark hallmarkNodeList=postgreDB.getHallmark_hallmark();
		
		visualizeNetworkWorker=new VisualizeNetworkWorker();
		ArrayList<Object> param=new ArrayList<Object>();
		param.add(gGraph);
		param.add(hallmarkNodeList);
		param.add(reapplySelectedHallmark);
		param.add(graphPanel);
		visualizeNetworkWorker.Start(VISUALIZE, param);
	}

	private void nodeList_valueChanged(ListSelectionEvent e) 
	{
		if (!e.getValueIsAdjusting()) 
		{
			if(nodeJList.getSelectedValue()!=null)
			{
				int selectedIndex = nodeJList.getSelectedIndex();
				if(selectedIndex!=-1)
				{
					node=nodeJList.getSelectedValue().toString();
					//update details of newly selected view
					setTitle("Neighbour(s) of "+node);
					nodeList=new ArrayList<String>();
					nodeList.add(node);
					neighbourList=postgreDB.getNetworkEdge_neighbourNameOfNode(node);
					neighbourList.retainAll(entireNodeList);
					for(int i=0; i<neighbourList.size(); i++)
						nodeList.add(neighbourList.get(i));
					inducedEdges=postgreDB.getNetworkEdge_inducedEdges(nodeList);
					//graphPanel.setLocation(new Point(5,5));
					if(osType==2)//MAC_OS
						graphPanel.setPreferredSize(new Dimension(800, 540));
					else
						graphPanel.setPreferredSize(new Dimension(600, 540));
					createGraph();
					gGraph.visualizeGraphNoSetLocation(graphPanel, false);
					gGraph.colorAndResizeSelectedNode(node, true);
				}
			}
		}
	}

	private void addVertexLegendHallmarkColor() 
	{
		addVertexLegendLabelColor(HALLMARK_PROLIFERATION_GUI, new Color(0x00FF00));
		addVertexLegendLabelColor(HALLMARK_GROWTH_REPRESSOR_GUI, new Color(0xFF8000));
		addVertexLegendLabelColor(HALLMARK_APOPTOSIS_GUI, new Color(0xFFFF00));
		addVertexLegendLabelColor(HALLMARK_REPLICATIVE_IMMORTALITY_GUI, new Color(0x0080FF));
		addVertexLegendLabelColor(HALLMARK_ANGIOGENESIS_GUI, new Color(0xFF80FF));
		addVertexLegendLabelColor(HALLMARK_METASTASIS_GUI, new Color(0x8080FF));
		addVertexLegendLabelColor(HALLMARK_METABOLISM_GUI, new Color(0x808000));
		addVertexLegendLabelColor(HALLMARK_IMMUNE_DESTRUCTION_GUI, new Color(0xFF0000));
		addVertexLegendLabelColor(HALLMARK_GENOME_INSTABILITY_GUI, new Color(0x008080));
		addVertexLegendLabelColor(HALLMARK_TUMOR_PROMOTING_INFLAMMATION_GUI, new Color(0xF5DEB3));
	}
	
	private void addVertexLegendLabelColor(String label, Color labelColor) 
	{
		JLabel legendLabel=new JLabel(label);
		JLabel legendLabelColor=new JLabel("    ");
		JPanel legendLabelColorPanel=new JPanel();

		legendLabelColor.setOpaque(true);
		legendLabelColor.setBackground(labelColor);
		legendLabelColorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		legendLabelColorPanel.add(legendLabelColor);
		legendLabelColorPanel.add(legendLabel);

		hallmarkLegendContent.add(legendLabelColorPanel);
	}
	
	private void initializeComponents()
	{
		JPanel topPanel=new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setPreferredSize(new Dimension(790, 540));
		
		graphPanel=new JPanel();
		graphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		graphPanel.setBackground(Color.BLACK);
		//graphPanel.setPreferredSize(new Dimension(600, 540));
		graphPanel.setPreferredSize(new Dimension(600, 540));
	
		JPanel nodeListPanel=new JPanel();
		nodeListPanel.setLayout(new BoxLayout(nodeListPanel, BoxLayout.Y_AXIS));
		nodeListPanel.setPreferredSize(new Dimension(150, 550));
		JPanel nodeListLabelPanel=new JPanel();
		nodeListLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		nodeListLabelPanel.setPreferredSize(new Dimension(150, 20));
		JLabel nodeListLabel=new JLabel("Node List:");
		nodeListLabelPanel.add(nodeListLabel);
		String[] formattedNodeList=new String[entireNodeList.size()];
		formattedNodeList = entireNodeList.toArray(formattedNodeList);
		nodeJList=new JList<String>();
		nodeJList.setListData(formattedNodeList);
		nodeScrollPane = new JScrollPane(nodeJList);
		nodeJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		nodeJList.setSelectedIndex(entireNodeList.indexOf(node));
		nodeJList.addListSelectionListener(new ListSelectionListener() 
		{
			public void valueChanged(ListSelectionEvent e){nodeList_valueChanged(e);}
		});
		nodeScrollPane.setViewportView(nodeJList);
		nodeScrollPane.setPreferredSize(new Dimension(150, 500));
		nodeScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		nodeListPanel.add(nodeListLabelPanel);
		nodeListPanel.add(nodeScrollPane);
				
		topPanel.add(graphPanel);
		topPanel.add(nodeListPanel);
		
		
		
		JPanel bottomPanel=new JPanel();
		//bottomPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setPreferredSize(new Dimension(900, 100));
		
		JPanel bottomLeftPanel=new JPanel();
		bottomLeftPanel.setLayout(new BoxLayout(bottomLeftPanel, BoxLayout.Y_AXIS));
		bottomLeftPanel.setPreferredSize(new Dimension(250, 100));
		JPanel hallmarkLegendLabelPanel=new JPanel();
		hallmarkLegendLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel hallmarkLegendLabel=new JLabel("Hallmark Legend:");
		hallmarkLegendLabelPanel.add(hallmarkLegendLabel);
		hallmarkLegendContent.setLayout(new BoxLayout(hallmarkLegendContent, BoxLayout.Y_AXIS));
		vertexLegendScroll.setPreferredSize(new Dimension(250, 100));
		addVertexLegendHallmarkColor();
		bottomLeftPanel.add(hallmarkLegendLabelPanel);
		bottomLeftPanel.add(vertexLegendScroll);
		
		JPanel bottomRightPanel=new JPanel();
		bottomRightPanel.setLayout(new BoxLayout(bottomRightPanel, BoxLayout.Y_AXIS));
		bottomRightPanel.setPreferredSize(new Dimension(600, 100));
		
		JPanel checkBoxesTopPanel=new JPanel();
		checkBoxesTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		checkBoxesTopPanel.setPreferredSize(new Dimension(600, 20));
		
		JPanel proliferationPanel=new JPanel();
		proliferationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(0))==true)
			proliferationCheckBox=new JCheckBox("Hallmark 1", true);
		else
			proliferationCheckBox=new JCheckBox("Hallmark 1", false);
		proliferationPanel.add(proliferationCheckBox);
		JPanel growthRepressorPanel=new JPanel();
		growthRepressorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(1))==true)
			growthRepressorCheckBox=new JCheckBox("Hallmark 2", true);
		else
			growthRepressorCheckBox=new JCheckBox("Hallmark 2", false);
		growthRepressorPanel.add(growthRepressorCheckBox);
		JPanel apoptosisPanel=new JPanel();
		apoptosisPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(2))==true)
			apoptosisCheckBox=new JCheckBox("Hallmark 3", true);
		else
			apoptosisCheckBox=new JCheckBox("Hallmark 3", false);
		apoptosisPanel.add(apoptosisCheckBox);
		JPanel replicativeImmortalityPanel=new JPanel();
		replicativeImmortalityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(3))==true)
			replicativeImmortalityCheckBox=new JCheckBox("Hallmark 4", true);
		else
			replicativeImmortalityCheckBox=new JCheckBox("Hallmark 4", false);
		replicativeImmortalityPanel.add(replicativeImmortalityCheckBox);
		JPanel angiogenesisPanel=new JPanel();
		angiogenesisPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(4))==true)
			angiogenesisCheckBox=new JCheckBox("Hallmark 5", true);
		else
			angiogenesisCheckBox=new JCheckBox("Hallmark 5", false);
		angiogenesisPanel.add(angiogenesisCheckBox);
		
		checkBoxesTopPanel.add(proliferationPanel);
		checkBoxesTopPanel.add(growthRepressorPanel);
		checkBoxesTopPanel.add(apoptosisPanel);
		checkBoxesTopPanel.add(replicativeImmortalityPanel);
		checkBoxesTopPanel.add(angiogenesisPanel);
		
		JPanel checkBoxesBottomPanel=new JPanel();
		checkBoxesBottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		checkBoxesBottomPanel.setPreferredSize(new Dimension(600, 20));
		
		JPanel metastasisPanel=new JPanel();
		metastasisPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(5))==true)
			metastasisCheckBox=new JCheckBox("Hallmark 6", true);
		else
			metastasisCheckBox=new JCheckBox("Hallmark 6", false);
		metastasisPanel.add(metastasisCheckBox);
		JPanel metabolismPanel=new JPanel();
		metabolismPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(6))==true)
			metabolismCheckBox=new JCheckBox("Hallmark 7", true);
		else
			metabolismCheckBox=new JCheckBox("Hallmark 7", false);
		metabolismPanel.add(metabolismCheckBox);
		JPanel immuneDestructionPanel=new JPanel();
		immuneDestructionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(7))==true)
			immuneDestructionCheckBox=new JCheckBox("Hallmark 8", true);
		else
			immuneDestructionCheckBox=new JCheckBox("Hallmark 8", false);
		immuneDestructionPanel.add(immuneDestructionCheckBox);
		JPanel genomeInstabilityPanel=new JPanel();
		genomeInstabilityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(8))==true)
			genomeInstabilityCheckBox=new JCheckBox("Hallmark 9", true);
		else
			genomeInstabilityCheckBox=new JCheckBox("Hallmark 9", false);
		genomeInstabilityPanel.add(genomeInstabilityCheckBox);
		JPanel tumorPromotingInflammationPanel=new JPanel();
		tumorPromotingInflammationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if(selectedHallmarks.contains(entireHallmarkList.get(9))==true)
			tumorPromotingInflammationCheckBox=new JCheckBox("Hallmark 10", true);
		else
			tumorPromotingInflammationCheckBox=new JCheckBox("Hallmark 10", false);
		tumorPromotingInflammationPanel.add(tumorPromotingInflammationCheckBox);
		
		checkBoxesBottomPanel.add(metastasisPanel);
		checkBoxesBottomPanel.add(metabolismPanel);
		checkBoxesBottomPanel.add(immuneDestructionPanel);
		checkBoxesBottomPanel.add(genomeInstabilityPanel);
		checkBoxesBottomPanel.add(tumorPromotingInflammationPanel);
		
		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		visualizeButton = new JButton();
		visualizeButton.setSize(50, 20);
		visualizeButton.setText(VISUALIZE);
		visualizeButton.setActionCommand(VISUALIZE);
		visualizeButton.addActionListener(actionListener);
		buttonPanel.add(visualizeButton);
		
		bottomRightPanel.add(checkBoxesTopPanel);
		bottomRightPanel.add(checkBoxesBottomPanel);
		bottomRightPanel.add(buttonPanel);
		
		bottomPanel.add(bottomLeftPanel);
		bottomPanel.add(bottomRightPanel);
		

		addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed. switch workspace back");
                interactivePC.openWorkspace(previousWorkspace); 
                setVisible(false);
            }
        });
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		add(topPanel);
		add(bottomPanel);
		setLocationByPlatform(true);
	}
	
	private void sendVirtualClickToRefreshGraphDisplay() 
	{
		System.out.println("send virtual click");
		Point currMousePosition=MouseInfo.getPointerInfo().getLocation();
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseMove(graphPanel.getLocationOnScreen().x+10, graphPanel.getLocationOnScreen().y+10);
			robot.mousePress(InputEvent.BUTTON1_MASK); //BUTTON1_MASK is the left button,
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mouseMove(currMousePosition.x, currMousePosition.y);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class VisualizeNetworkWorker
	{
		Thread worker;
		boolean working;
		String ACTION;
		ArrayList<Object> paramList;

		VisualizeNetworkWorker()
		{
			worker=new Thread(new Runnable(){
				public void run(){
					if(ACTION.compareTo(VISUALIZE)==0)
					{
						working=true;
						Gephi_graph graph=(Gephi_graph)paramList.get(0);
						allHallmark hallmarkNodeList=(allHallmark)paramList.get(1);
						ArrayList<String> hallmarkMask=(ArrayList<String>)paramList.get(2);
						JPanel gPanel=(JPanel)paramList.get(3);
						
						//uncolor all previous hallmarks
						graph.resetHallmarkNode();
						//color selected hallmarks
						System.out.println("hallmarkMask:"+hallmarkMask.toString());
						graph.colorAndResizeHallmarkNode(hallmarkNodeList, hallmarkMask, true);
						sendVirtualClickToRefreshGraphDisplay();
						working=false;
					}
				}
			});
		}
		public void Start(String action, ArrayList<Object> parameterList)
		{
			if(!working)
			{
				ACTION=action;
				paramList=parameterList;
				worker.start();
			}
		}
	}
}
