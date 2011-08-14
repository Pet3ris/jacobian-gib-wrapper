jacobian GiB wrapper
====================

A GiB engine wrapper for the jacobian protocol (see [jacobian-host](https://github.com/Pet3ris/jacobian-host)).

Requirements
------------

* wine

 
Usage
-----

First place `gib.exe` (if you have the commercial `bridge.exe`, rename it to `gib.exe`)
in the same directory where the `jacobian-gib-wrapper.jar` is placed.

An old variant of the gib engine was made available a while ago.
It has since been removed from the public archive so I am not distributing it with the wrapper.

Building (not necessary):
    
    $ sh build.sh

Running:

    $ java -jar jacobian-gib-wrapper.jar
