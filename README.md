# Baumfalk_MA_MUML_Pipeline
Dies ist das Repository zu der Masterarbeit "Eine modellgetriebene Automatisierung von Code-Generation, -Integration und -Deployment von autonomen Fahrfunktionen".
Sie enthält den Code, die Diagramme (auch die, die zu groß für die Ausarbeitung sind), eine Kopie der verwendeten Arduino-CLI (Version 0.35.3) sowie die Installationsskripte.
Die Arduino-IDE wird ebenfalls installiert und gestartet, um Probleme mit fehlenden Treibern und Zugriffsrechten zu vermeiden.
Hierbei muss die erste Ausführung nur mit normalen Rechten, also ohne sudo-Anweisung, erfolgen.

Nicht zu Windows kompatibel, weil dieses standardmäßig keine Unterscheidung der Groß- und Klein-Schreibung in dem Dateisystem verwendet und  deshalb alles für Ubuntu entworfen wurde. 

Bei unerklärlichen Fehlern wie einem fehlenden bzw. nicht generierten lib-Ordner muss der Workspace neu aufgesetzt (oder alles entfernt) und neu importiert werden, weil die Eclipse-IDE manchmal beim Arbeiten mit den MUML-Plug-Ins versagt und dann keinen vollständig funktionierenden Zustand produzieren kann.

Die verwendete Version von Sofd-Car-HAL ist in dem Projekt Overtaking-Cars-Baumfalk hinterlegt, um es über die Fähigkeiten von ArduinoCLIUtilizer zu installieren.

# Installation:

# MUML-Plug-Ins
Die folgendenRepositories herunterladen (RepositoryDownloader.sh automatisiert dies) und in Eclipse einfach komplett, d.h. die Plug-In-Projekte und ihre unter-Projekte importieren (File -> Import -> Git -> Projects from Git -> Existing local repository -> (MUMLPlugins oder entsprechenden anderen Ort auswählen)).
* https://github.com/fraunhofer-iem/mechatronicuml-core (branch: master)
* https://github.com/fraunhofer-iem/mechatronicuml-pim (branch: master)
* https://github.com/fraunhofer-iem/mechatronicuml-pm (branch: master)
* https://github.com/fraunhofer-iem/mechatronicuml-psm/tree/stuerner_ma (branch: stuerner_ma)
* https://github.com/fraunhofer-iem/mechatronicuml-cadapter-component-type (branch: stuerner_ma)

Aus diesem Repository aus dem Ordner Plug-Ins alle Plug-In-Projekte, die von Eclipse erkannt werden
(File -> Import -> Git -> Projects from Git -> Existing local repository -> (Der Ordner von diesem Repository)),
importieren. Dies schließt die Unterprojekte der modifizierten Version von mechatronicuml-cadapter-component-container ein.


#Wichtige Links:

## MUML-Plug-In mechatronicuml-cadapter-component-container:
https://github.com/fraunhofer-iem/mechatronicuml-cadapter-component-container/tree/stuerner_ma
Das MUML-Plug-In mechatronicuml-cadapter-component-container wurde nicht von mir entwickelt, sondern übernommen und modifiziert.

## Ursprung vom MUML-Projekt Overtaking-Cars:
https://github.com/SQA-Robo-Lab/Overtaking-Cars/tree/hal_demo
Ich habe dies übernommen und modifiziert.
Der Namensanhang Baumfalk dient der Unterscheidung.

## Installationsanweisungen für MUML:
https://github.com/SQA-Robo-Lab/MUML_1_0-win32-x86_64?tab=readme-ov-file
Hierbei muss jedoch mechatronicuml-cadapter-component-container  nicht heruntergeladen werden, weil eine modifizierte Version in diesem Repository unter Plug-Ins/Modifiziert bereit liegt.
