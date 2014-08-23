GecoSI
======

Copyright (c) 2013-2014 Simon Denier.

Open-source Java library to use the SPORTident timing system.
Developed for Geco http://github.com/sdenier/Geco

Distributed under the MIT license (see LICENSE file).

Some parts released by SPORTident under the CC BY 3.0 license. http://creativecommons.org/licenses/by/3.0/

C#.Net Port
-----------

Thanks to Yannis Guedel, there is a port of [GecoSI](https://github.com/yannisgu/GecoSI.Net) to the .Net framework.

Changelog
=========

v1.2.0
------

- Support for pCard

v1.1.0
------

- Faster and more reliable card readouts for SI6/6*/10/11/SIAC
- Check that station is in handshake mode at startup (as well as using the extended protocol)
- Fix bug when reading an empty card

v1.0.0
------

- Only support extended protocol (BSx7/BSx8 stations with firmware 580+), no base protocol support
- Support handshake mode, not autosend
- Support for SI5/6/6*/8/9/10/11/SIAC
- Support for 192 punches mode

Build Target
============

```
  ant build_jar
```

Usage (Library)
===============

- `SiHandler#connect` is the entry point (see `#main` for a basic client)
- Client should provide a `SiListener` implementation to `SiHandler`
- `SiListener` is notified with station status (`CommStatus`) and SiCard dataframes (`SiDataFrame` and `SiPunch`)
- That's all you need to know!

Usage (CLI)
===========

- `SiHandler` can be run from the command line with `java net.gecosi.SiHandler`
- It provides a simple handler which prints events, status, and sicard data as they are read
- It takes as a parameter the serial port to connect to
- Alternatively, it can take '--file <logfilename>' to re-read a log file created by GecoSI

Logging
=======

Logging can be setup with the system property `GECOSI_LOG`

```
  java -DGECOSI_LOG=OUTSTREAM net.gecosi.SiHandler COM8
```

- `GECOSI_LOG=FILE` - log to file gecosi.log (default setup)
- `GECOSI_LOG=OUTSTREAM` - log to console
- `GECOSI_LOG=NONE` - log nothing
