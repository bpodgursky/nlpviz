<!DOCTYPE html>

<html lang="en">

<head>

  <title>${param.page_title}</title>

  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" type="text/css" href="resources/jquery-ui-1.9.2.custom.css">
  <link rel="stylesheet" type="text/css" href="resources/font-awesome.min.css">

  <link rel="stylesheet" type="text/css" href="resources/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="css/dagre-d3-simple.css">
  <link rel="stylesheet" type="text/css" href="css/digraph.css">

  <script type="text/javascript" src="resources/jquery-2.0.0.min.js"></script>
  <script type="text/javascript" src="resources/jquery.form.min.js"></script>
  <script type="text/javascript" src="resources/marked.js"></script>
  <script type="text/javascript" src="resources/purl.js"></script>
  <script type="text/javascript" src="resources/d3.min.js"></script>
  <script type="text/javascript" src="resources/dagre.js"></script>
  <script src="resources/dagre-d3-simple.js"></script>

</head>

<body>

<div>
  <label>
    <textarea placeholder="This is an example sentence." id="sentence_input" class="text-input" rows="3"></textarea>
  </label>
  <%--TODO why am I doing this manually.  This is like living in the dark ages--%>

  <div class="row-fluid row">
    <div class="span10 mycontent-left">
      <svg>
        <g transform="translate(20, 15)">
          <rect class="legend" style="fill: #b997ff" rx="5" ry="5" width="70" height="37"></rect>
          <g transform="translate(10, 10)">
            <text>
              <tspan dy="1em">Person</tspan>
            </text>
          </g>
        </g>
        <g transform="translate(110, 15)">
          <rect class="legend" style="fill: #ffae6a" rx="5" ry="5" width="50" height="37"></rect>
          <g transform="translate(10, 10)">
            <text>
              <tspan dy="1em">Date</tspan>
            </text>
          </g>
        </g>
        <g transform="translate(180, 15)">
          <rect class="legend" style="fill: #96c2ff" rx="5" ry="5" width="100" height="37"></rect>
          <g transform="translate(10, 10)">
            <text>
              <tspan dy="1em">Organization</tspan>
            </text>
          </g>
        </g>
        <g transform="translate(300, 15)">
          <rect class="legend" style="fill: #7e7e7e" rx="5" ry="5" width="80" height="37"></rect>
          <g transform="translate(10, 10)">
            <text>
              <tspan dy="1em">Location</tspan>
            </text>
          </g>
        </g>
        <g transform="translate(400, 15)">
          <rect class="legend" style="fill: #92ff7d" rx="5" ry="5" width="70" height="37"></rect>
          <g transform="translate(10, 10)">
            <text>
              <tspan dy="1em">Ordinal</tspan>
            </text>
          </g>
        </g>
        <g transform="translate(490, 15)">
          <rect class="legend" style="fill: #fdb9ff" rx="5" ry="5" width="70" height="37"></rect>
          <g transform="translate(10, 10)">
            <text>
              <tspan dy="1em">Number</tspan>
            </text>
          </g>
        </g>
      </svg>

    </div>
    <div class="span2">
      <div style="text-align: center;">
        By <a href="http://bpodgursky.wordpress.com/">Ben Podgursky</a>
        <span></span>
      </div>
    </div>

  </div>

  <%--</div>--%>
</div>

<div id="attach">
  <svg class="main-svg" id="svg-canvas"></svg>
</div>



<script>

  var legendValues = {
    0: {
      label: "Organization", nodeclass: "legend-organization"
    },
    1: {
      label: "Person", nodeclass: "legend-person"
    }
  };

  renderJSObjsToD3(legendValues, [], ".main-svg");


  var input = $("#sentence_input");
  input.keypress(function (e) {
    if (e.which == 13) {
      renderText(input.val());
      e.preventDefault();
    }
  });

  function renderText(text) {
    $.ajax({
      type: 'GET',
      dataType: 'html',
      url: "parser",
      data: {
        text: text
      },
      success: function (data) {
        var dataParsed = JSON.parse(data);
        console.log(dataParsed);

        var nodes = {};
        var edges = [];

        dataParsed.forEach(function (e) {
          populate(e, nodes, edges);
        });

        console.log(JSON.stringify(nodes));
        console.log(JSON.stringify(edges));

        renderJSObjsToD3(nodes, edges, ".main-svg");
      }
    });
  }

  function populate(data, nodes, edges) {
    var nodeID = Object.keys(nodes).length;

    var newNode = {
      label: (data.data.type == "TK") ? data.data.word : data.data.type,
      id: nodeID + ""
    };

    var classes = ["type-" + data.data.type];
    if (data.data.ne) {
      classes.push("ne-" + data.data.ne);
    }

    newNode.nodeclass = classes.join(" ");

    //  I hate javascript
    nodes[nodeID] = newNode;

    data.children.forEach(function (child) {
      var newChild = populate(child, nodes, edges);

      edges.push({
        source: newNode.id,
        target: newChild.id,
        id: newNode.id + "-" + newChild.id
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

  renderText("This is an example sentence.");

</script>

</body>

</html>
