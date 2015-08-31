function renderText(text) {
    $.ajax({
	type: 'GET',
	dataType: 'html',
	url: "parser",
	data: {
            text: text
	},
	success: function (data) {

            $("#svg-canvas").empty();

            var dataParsed = JSON.parse(data);
            var nodes = {};
            var edges = [];

            dataParsed.forEach(function (e) {
		populate(e, nodes, edges);
            });

            var g = new dagreD3.graphlib.Graph()
		.setGraph({})
		.setDefaultEdgeLabel(function () {
		    return {};
		});

            for (var key in nodes) {
		var node = nodes[key];
		g.setNode(node.id, {
		    label: node.label,
		    class: node.nodeclass,
		    //  round edges
		    rx: 5,
		    ry: 5
		});
            }

            edges.forEach(function (e) {
		g.setEdge(e.source, e.target, {
		    lineTension: .8,
		    lineInterpolate: "bundle"
		});
            });

            var render = new dagreD3.render();
            var svg = d3.select("#svg-canvas"),
            svgGroup = svg.append("g");

            render(d3.select("#svg-canvas g"), g);

            var xCenterOffset = (svg.attr("width") - g.graph().width) / 2;
            svgGroup.attr("transform", "translate(" + xCenterOffset + ", 20)");
            svg.attr("height", g.graph().height + 40);

            //  enable zoom and scrolling
            svgGroup.attr("transform", "translate(5, 5)");
            svg.call(d3.behavior.zoom().on("zoom", function redraw() {
		svgGroup.attr("transform",
			      "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
            }));
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
