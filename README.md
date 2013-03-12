GecoSI
======

Copyright (c) 2013 Simon Denier

Open-source Java library to use the SPORTident timing system.
Developed for Geco http://github.com/sdenier/Geco

Distributed under the MIT license (see LICENSE file).

Specifications
==============

- Only support extended protocol (BSx7/BSx8 stations), no base protocol support
- Support handshake mode, not autosend
- Support for SI5/6/6*/8/9/10/11
- Later: support for memory backup readout

Process
=======

- comm reader handles comm event and processes messages
- driver thread handles messages and communicates with station to download sicard data
- notifier notifies client and comm status

Failure Handling
================

- robust startup: detect baud rate, check extended protocol
- USB disconnect? I/O error
- CRC error, protocol error, timeout, ressource
- comm status update and thread interruption
