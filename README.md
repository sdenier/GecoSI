GecoSI
======

Copyright (c) 2013 Simon Denier.

Open-source Java library to use the SPORTident timing system.
Developed for Geco http://github.com/sdenier/Geco

Distributed under the MIT license (see LICENSE file).

Some parts released by SPORTident under the CC BY 3.0 license. http://creativecommons.org/licenses/by/3.0/

Specifications
==============

- Only support extended protocol (BSx7/BSx8 stations with firmware 580+), no base protocol support
- Support handshake mode, not autosend
- Support for SI5/6/6*/8/9/10/11
- Later: support for memory backup readout

Usage
=====

- `SiHandler#connect` is the entry point (see `#main` for a basic client)
- Client should provide a `SiListener` implementation to `SiHandler`
- `SiListener` is notified with station status (`CommStatus`) and SiCard dataframes (`SiDataFrame` and `SiPunch`)
- That's all you need to know!
