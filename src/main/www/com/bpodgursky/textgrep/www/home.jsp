<!DOCTYPE html>

<html lang="en">

<head>

  <title>${param.page_title}</title>

  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" type="text/css" href="resources/jquery-ui-1.9.2.custom.css">
  <link rel="stylesheet" type="text/css" href="resources/font-awesome.min.css">
  <link rel="stylesheet" type="text/css" href="resources/bootstrap.min.css" media="screen">

  <script type="text/javascript" src="resources/jquery-2.0.0.min.js"></script>
  <script type="text/javascript" src="resources/bootstrap.min.js"></script>
  <script type="text/javascript" src="resources/jquery.form.min.js"></script>
  <script type="text/javascript" src="resources/marked.js"></script>
  <script type="text/javascript" src="resources/purl.js"></script>
  <script type="text/javascript" src="resources/d3.min.js"></script>

  <style>

    .node {
      stroke: #fff;
      stroke-width: 1.5px;
    }

    .node {
      font: 10px sans-serif;
    }

    .link {
      stroke: #999;
      stroke-opacity: .6;
    }

  </style>

</head>

<body>

<div class="input-small">
  <label>
    <input type="search" placeholder="Sentence" id="sentence_input" class="input-large">
  </label>
</div>

<div id="attach">

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
        console.log(dataParsed);

        var nodes = [];
        var links = [];
        buildGraphData(dataParsed, nodes, links);

        console.log(nodes);
        console.log(links);

        renderGraph({
          nodes: nodes,
          links: links
        });
      }
    });
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

  function renderGraph(graph) {

    $("#d3-graph").remove();

    var currWindow = $(window);
    var width = currWindow.width(),
        height = currWindow.height();

    var color = d3.scale.category20();

    var force = d3.layout.force()
        .charge(-120)
        .linkDistance(30)
        .size([width, height]);

    var svg = d3.select("body").append("svg")
        .attr("width", width)
        .attr("height", height)
        .attr("id", "d3-graph");

    force
        .nodes(graph.nodes)
        .links(graph.links)
        .start();

    var link = svg.selectAll(".link")
        .data(graph.links)
        .enter().append("line")
        .attr("class", "link")
        .style("stroke-width", function (d) {
          return Math.sqrt(d.value);
        });

    var node = svg.selectAll(".node")
        .data(graph.nodes)
        .enter().append("circle")
        .attr("class", "node")
        .attr("r", 5)
        .style("fill", function (d) {
          return color(d.group);
        })
        .call(force.drag);

    node.append("title")
        .text(function (d) {
          return d.name;
        });

    node.append("text")
        .attr("dx", function (d) {
          return -8
        })
        .attr("dy", 3)
        .style("text-anchor", function (d) {
          return "end"
        })
        .text(function (d) {
          return d.name;
        });

    force.on("tick", function () {
      link.attr("x1", function (d) {
        return d.source.x;
      })
          .attr("y1", function (d) {
            return d.source.y;
          })
          .attr("x2", function (d) {
            return d.target.x;
          })
          .attr("y2", function (d) {
            return d.target.y;
          });

      node.attr("cx", function (d) {
        return d.x;
      })
          .attr("cy", function (d) {
            return d.y;
          });
    });

  }

  renderSentence("This is an example sentence.");


</script>

</body>

</html>
