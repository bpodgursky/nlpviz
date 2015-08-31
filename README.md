nlpviz
======

Source code for a sentence parse tree visualiation found here: http://nlpviz.bpodgursky.com/home

Getting Started
---------------

Build the code with:

`mvn compile`

The first time you run this, it will take some time and network bandwidth to install the
dependencies - the Stanford NLP core model jar is on its own over 200 MB.

Next, start the parsing servlet with:

`mvn exec:java`

At the end of the output you should see:

```
15/08/30 20:13:29 INFO com.bpodgursky.nlpviz.api.WebServer: Parse Server is listening on port: 43315
```

At this point, you can open [http://localhost:43315](http://localhost:43315) in your browser.

