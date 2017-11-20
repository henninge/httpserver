HTTP Server
===========

Usage
-----

To start the httpserver app, run the jar file:

    java -jar httpserver.jar

The server runs on port 8080, serving files from the current directory. These
values can be changed via command line parameters.

    java -jar httpserver.jar /path/to/docroot 7070

Features
--------

The server will return directoy listings as HTML pages with links to the
contained files and directories. Clicking on the link for a directory will
open the listing for this directory, clicking on the link for a file will
initiate a download for the file.

File download is aided by a "Content-Disposition" header that includes the
file name as well as a "Content-Length" header to enable progress monitoring.

Invalid paths will result in "404 Not Found" responses.

The server will return "Last-Modified" and "Etag" headers to fascilitate
effective caching and therefore supports conditional requests via the
"If-Match", "If-None-Match", and "If-Modified-Since" request headers.

The server supports HEAD requests.

Limitations
-----------

The server only supports GET and HEAD requests. All other requests are
answered with "501 Not Implemented".

Persistent (keep-alive) connections are not enabled, each request will contain
the "Connection: close" header. (1)


Implementation Notes
--------------------

The high-level processing of HTTP requests goes something like this.

  1. Open a listening TCP socket (HttpServer).
  2. Accept a connection and hand it to a thread for processing (HttpServerThread).
  3. Read and parse the request from the connection (HttpRequest).
  4. Select the type of response to send (HttpHandler).
  5. Build and send the response (HttpResponse).
  6. If the connection is persistent (1), repeat from step 3.
  7. Close the connection and end the thread.

A classical HTTP framework would define different handlers (views) for
different URLs, called "Routing" or "Traversal". These handlers would then
build the appropriate response. I used a simplified version of this due to the
simple nature of this server. This implementation only uses one handler
and defines different types of responses which contain the respective logic.
It is the handler's responsibilty to select the correct response.

The HttpRequest object encapsulates all information about the request (method,
path, headers). The HttpResponse objects encapsulate all information needed
to send the reponse (status, headers) and implements a method to write the
response to the socket.

The specific reponses (DirectoryResponse, FileResponse, StatusResponse) are
sub-classes of HttpResponse. Since all information needed to build the
response is found in the request and the filesystem, most of the logic is found 
in the constructors, setting response-specific headers. The subclasses
implement writeBody which writes the response-specific body to the socket.
The base class will suppress the body if needed (HEAD requests, NotModified
responses).


Developer Notes
---------------

The chosen implemenation is aimed to be closely aligned to the HTTP protocol
so that the different elements (requests & responses, status & headers) are
clear to anyone famliar with HTTP.

I took the basic structure of how to use ServerSocket, Socket and Thread
from the respective [JDK tutorial sample](https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html).
All other code was conceived and written by me, sometimes taking hints from
code snippets found in the JDK reference.

This was my first Java project in over ten years so a lot of my time was spent
reading the JDK reference and the ocasional google search, as well as the HTTP
specification. Due to that I decided to do without unit tests and proper
javadoc which I would usually include.

(1) I had tried the implementation for persistent connections  for file
downloads but had to disable it again. The HTTP clients (curl, Chrome) were
not able to detect the end of the response unless the connection was closed.
I could not find if this is a bug in the implementation or if my understaning
of how persistent connections are supposed to work is wrong. Also, I am aware
that the current implementation is not complete with respect to the
"Keep-Alive" header's parameters (timeout, max).