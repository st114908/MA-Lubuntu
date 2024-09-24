sudo apt update
sudo apt install git
mkdir -p /home/muml/MUMLPlugins
cd /home/muml/MUMLPlugins
git clone https://github.com/fraunhofer-iem/mechatronicuml-core
git clone https://github.com/fraunhofer-iem/mechatronicuml-pim
git clone https://github.com/fraunhofer-iem/mechatronicuml-pm
git clone https://github.com/fraunhofer-iem/mechatronicuml-psm
cd mechatronicuml-psm
git checkout stuerner_ma
cd ..
git clone https://github.com/fraunhofer-iem/mechatronicuml-cadapter-component-type
cd mechatronicuml-cadapter-component-type
git checkout stuerner_ma