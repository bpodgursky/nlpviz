<!DOCTYPE html>

<html lang="en">

<head>

  <title>${param.page_title}</title>

  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" type="text/css" href="resources/jquery-ui-1.9.2.custom.css">
  <link rel="stylesheet" type="text/css" href="resources/font-awesome.min.css">
  <link rel="stylesheet" type="text/css" href="resources/bootstrap.min.css" media="screen">
  <link rel="stylesheet" type="text/css" href="css/digraph.css">

  <script type="text/javascript" src="resources/jquery-2.0.0.min.js"></script>
  <script type="text/javascript" src="resources/bootstrap.min.js"></script>
  <script type="text/javascript" src="resources/jquery.form.min.js"></script>
  <script type="text/javascript" src="resources/marked.js"></script>
  <script type="text/javascript" src="resources/purl.js"></script>
  <script type="text/javascript" src="resources/d3.min.js"></script>
  <script type="text/javascript" src="resources/dagre.js"></script>

  <script src="js/digraph.js"></script>

</head>

<body>

<div class="input-small">
  <label>
    <input type="search" placeholder="This is an example sentence." id="sentence_input" class="input-large">
  </label>
</div>

<div id="attach">
  <svg id="svg-canvas" width=800 height=600>
    <defs>
      <marker id="arrowhead"
              viewBox="0 0 10 10"
              refX="8"
              refY="5"
              markerUnits="strokeWidth"
              markerWidth="8"
              markerHeight="5"
              orient="auto"
              style="fill: #333">
        <path d="M 0 0 L 10 5 L 0 10 z"></path>
      </marker>
    </defs>
  </svg>
</div>

<script>

  var input = $("#sentence_input");
  input.keypress(function (e) {
    if (e.which == 13) {
      renderSentence(input.val());
      e.preventDefault();
    }
  });

  function renderSentence(sentence) {
    $.ajax({
      type: 'GET',
      dataType: 'html',
      url: "parser",
      data: {
        sentence: sentence
      },
      success: function (data) {
        var dataParsed = JSON.parse(data);

        var nodes = [];
        var edges = [];

        populate(dataParsed, nodes, edges);

        var svg = d3.select("svg");
        svg.selectAll("*").remove();
        drawObjs(nodes, edges, svg);
      }
    });
  }

  function populate(data, nodes, edges) {
    var newNode = {
      label: (data.data.type == "TK")? data.data.content: data.data.type,
      id: nodes.length + ""
    };

    nodes.push(newNode);

    data.children.forEach(function (child) {
      var newChild = populate(child, nodes, edges);

      edges.push({
        source: newNode,
        target: newChild,
        id: newNode.id+"-"+newChild.id
      });
    });

    return newNode;
  }


  function buildGraphData(node, nodes, links) {

    var index = nodes.length;
    nodes.push({
      name: node.data.content,
      group: 1
    });

    node.children.forEach(function (e) {
      links.push({
        source: index,
        target: nodes.length,
        value: 2
      });
      buildGraphData(e, nodes, links);
    });
  }

  renderSentence("This is an example sentence.");

</script>

</body>

</html>
