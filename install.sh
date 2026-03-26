#!/bin/bash
set -e

echo "Building pulse..."
mvn package -q -DskipTests

echo "Installing..."
mkdir -p ~/.pulse
cp target/pulse.jar ~/.pulse/pulse.jar

cat > /usr/local/bin/pulse << 'WRAPPER'
#!/bin/bash
java -jar ~/.pulse/pulse.jar "$@"
WRAPPER

chmod +x /usr/local/bin/pulse

echo ""
echo "✓ pulse instalado com sucesso!"
echo "  Rode: pulse check --help"
