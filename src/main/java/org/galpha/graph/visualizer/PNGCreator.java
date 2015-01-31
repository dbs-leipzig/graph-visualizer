package org.galpha.graph.visualizer;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.openord.OpenOrdLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.presets.BlackBackground;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 *
 */
public class PNGCreator {
  private String inputDot;
  private DirectedGraph graph;
  private GraphModel graphModel;
  private AttributeModel attributeModel;
  private AttributeColumn color;

  private static final Pattern comma = Pattern.compile(",");


  public PNGCreator(String inputDot) {
    this.inputDot = inputDot;
  }

  private void setup() {
    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    pc.newProject();
    Workspace workspace = pc.getCurrentWorkspace();
    ImportController importController =
      Lookup.getDefault().lookup(ImportController.class);
    this.graphModel =
      Lookup.getDefault().lookup(GraphController.class).getModel();
    this.attributeModel =
      Lookup.getDefault().lookup(AttributeController.class).getModel();
    Container container;
    try {
      File file = new File(inputDot);
      container = importController.importFile(file);
      container.getLoader()
        .setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
    } catch (Exception ex) {
      ex.printStackTrace();
      return;
    }
    importController.process(container, new DefaultProcessor(), workspace);
    this.graph = graphModel.getDirectedGraph();
  }

  private void computeLayout() {


//    AutoLayout autoLayout = new AutoLayout(10, TimeUnit.SECONDS);
//    autoLayout.setGraphModel(graphModel);
//    ForceAtlas2 firstLayout = new ForceAtlas2(null);
//    autoLayout.addLayout(firstLayout, 1f);
//    autoLayout.execute();


    AutoLayout autoLayout = new AutoLayout(10, TimeUnit.SECONDS);
    autoLayout.setGraphModel(graphModel);
    OpenOrdLayout firstLayout = new OpenOrdLayout(null);
    firstLayout.setRandSeed((long) 575546861);
    firstLayout.setEdgeCut(0.5f);
    firstLayout.setNumIterations(500);
    autoLayout.addLayout(firstLayout, 1f);
    autoLayout.execute();
  }

  private void setNodeColors() {
    AttributeColumn[] hex = attributeModel.getNodeTable().getColumns();
    int arr$ = hex.length;
    int len$;
    for (len$ = 0; len$ < arr$; ++len$) {
      AttributeColumn i$ = hex[len$];
      if (i$.getId().toLowerCase().contains("color")) {
        this.color = i$;
        break;
      }
    }
    Node[] var15 = graph.getNodes().toArray();
    len$ = var15.length;
    for (int var16 = 0; var16 < len$; ++var16) {
      Node node = var15[var16];
      String colorString = (String) node.getNodeData().getAttributes()
        .getValue(this.color.getIndex());
      if (colorString.contains(comma.pattern())) {
        String[] RGB = comma.split(colorString);
        node.getNodeData().setR(Float.valueOf(RGB[0]) / 255.0F);
        node.getNodeData().setG(Float.valueOf(RGB[1]) / 255.0F);
        node.getNodeData().setB(Float.valueOf(RGB[2]) / 255.0F);
      } else {
        Color var14 = Color.decode(colorString);
        node.getNodeData().setR((float) var14.getRed() / 255.0F);
        node.getNodeData().setG((float) var14.getGreen() / 255.0F);
        node.getNodeData().setB((float) var14.getBlue() / 255.0F);
      }
    }
  }

  private void exportPNG() {

    PreviewModel model =
      Lookup.getDefault().lookup(PreviewController.class).getModel();

    PreviewProperties prop = model.getProperties();
    prop.putValue(PreviewProperty.EDGE_CURVED, false);
    prop.putValue(PreviewProperty.EDGE_THICKNESS, 0.5f);

    ExportController ec = Lookup.getDefault().lookup(ExportController.class);
    String destPath = inputDot.replace(".dot", ".png");
    try {
      ec.exportFile(new File(destPath));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void createPNG() {
    setup();
    computeLayout();
    setNodeColors();
    exportPNG();
  }
}
