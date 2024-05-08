# Container Code Generation

This repository implements the generation of platform-specific code, so-called "container code". It is part of the code generation approach for multi-ECU deployments as suggested in [Pohlmann's "A model-driven software construction approach for cyber-pyhsical systems"](https://digital.ub.uni-paderborn.de/ubpb/urn/urn:nbn:de:hbz:466:2-30659). The container code is responsible to bridge between the platform-independent component code and the underlying platform including its operation system or APIs. The container code generation requires a MechatronicUML DeplyomentConfiguration, which is part of the Platform-Specific Modeling.

* For the component code generation, see: https://github.com/fraunhofer-iem/mechatronicuml-cadapter-component-type
* For the platform-specific modeling, see: https://github.com/fraunhofer-iem/mechatronicuml-psm

This repository was last updated during a Master's thesis that added containers for Arduino. It contains the following artifacts:
* org.muml.arduino.adapter.container.ui: An Eclipse-IDE plugin with UI funcitonality to use the Arduino container code generator
* org.muml.arduino.adapter.container: An Acceleo project implementing the model-to-text generation that produces the Arduino container code
* org.muml.c.adapter.container.ui: An Eclipse-IDE plugin with UI funcitonality to use the C container code generator
* org.muml.c.adapter.container: An Acceleo project implementing the model-to-text generation that produces the C container code