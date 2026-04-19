package id.naturalsmp.NaturalWorldGen.util.matter.slices;

import id.naturalsmp.NaturalWorldGen.core.link.Identifier;
import id.naturalsmp.NaturalWorldGen.util.data.palette.Palette;
import id.naturalsmp.NaturalWorldGen.util.matter.Sliced;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Sliced
public class IdentifierMatter extends RawMatter<Identifier> {

	public IdentifierMatter() {
		this(1, 1, 1);
	}

	public IdentifierMatter(int width, int height, int depth) {
		super(width, height, depth, Identifier.class);
	}

	@Override
	public Palette<Identifier> getGlobalPalette() {
		return null;
	}

	@Override
	public void writeNode(Identifier b, DataOutputStream dos) throws IOException {
		dos.writeUTF(b.toString());
	}

	@Override
	public Identifier readNode(DataInputStream din) throws IOException {
		return Identifier.fromString(din.readUTF());
	}
}
