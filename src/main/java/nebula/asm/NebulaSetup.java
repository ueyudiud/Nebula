/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.asm;

import static org.objectweb.asm.Opcodes.*;

import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.*;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;

import nebula.NebulaLoadingPlugin;
import nebula.base.J;
import nebula.common.util.Jsons;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

/**
 * @author ueyudiud
 */
public class NebulaSetup implements IFMLCallHook
{
	/**
	 * The ASM file version, uses to determine if it need replaced ASM files.
	 */
	private static final int VERSION = 19;
	
	final JsonDeserializer<OpInfo>				DESERIALIZER1	= (json, typeOfT, context) -> {
		if (!json.isJsonObject()) throw new JsonParseException("The json should be an object.");
		JsonObject object = json.getAsJsonObject();
		OpInfo information = new OpInfo(object.get("name").getAsString());
		try
		{
			if (!object.has("modification"))
			{
				NebulaASMLogHelper.LOG.warn("No modification of " + information.mcpname + " detected.");
			}
			else
			{
				if (object.has("values"))
				{
					JsonArray array = object.getAsJsonArray("values");
					for (JsonElement json1 : array)
						context.deserialize(json1, Function.class);
				}
				JsonArray array = object.getAsJsonArray("modification");
				for (JsonElement json1 : array)
				{
					JsonObject object1 = json1.getAsJsonObject();
					String name = object1.get("name").getAsString();
					if (Jsons.getOrDefault(object1, "remove", false))
					{
						information.modifies.put(name, ImmutableList.of());
						continue;
					}
					
					{
						List<OpLabel> list = Jsons.getAsList(object1.getAsJsonArray("labels"), json2 -> context.deserialize(json2, OpLabel.class));
						if (!list.isEmpty())
						{
							information.modifies.put(name, list);
						}
					}
				}
			}
		}
		catch (RuntimeException exception)
		{
			throw new JsonParseException("Can not parse asm config of " + information.mcpname, exception);
		}
		return information;
	};
	final JsonDeserializer<OpLabel>				DESERIALIZER2	= (json, typeOfT, context) -> {
		if (!json.isJsonObject()) throw new JsonParseException("The json should be an object.");
		JsonObject object = json.getAsJsonObject();
		OpType type = OpType.parseValue(object.get("type").getAsString());
		int off = J.getOrDefault(object, "off", 0);
		int len = J.getOrDefault(object, "len", 1);
		List<AbstractInsnNode> nodes;
		if (object.has("nodes"))
		{
			nodes = new ArrayList<>();
			for (JsonElement element : object.getAsJsonArray("nodes"))
			{
				AbstractInsnNode node = context.deserialize(element, AbstractInsnNode.class);
				if (node == null) throw new JsonParseException("No node exist!");
				nodes.add(node);
			}
		}
		else
			nodes = null;
		OpLabel label;
		if (object.has("name"))
		{
			String owner = object.get("owner").getAsString();
			String name = object.get("name").getAsString();
			String desc = object.get("desc").getAsString();
			int count = J.getOrDefault(object, "count", 1);
			// Each prefer first for replacement.
			label = new OpLabel.OpLabelMethodAsTag(count, owner, name, desc, off, len, type, nodes);
		}
		else
			switch (J.getOrDefault(object, "marker", -1))
			{
			case 1:
				label = new OpLabel.OpLabelBegining(off, len, type, nodes);
				break;
			default:
				int line = J.getOrDefault(object, "line", 0);
				label = new OpLabel.OpLabelLineNumber(line, off, len, type, nodes);
				break;
			}
		return label;
	};
	final JsonDeserializer<AbstractInsnNode>	DESERIALIZER3	= (json, typeOfT, context) -> {
		if (!json.isJsonObject())
		{
			return new InsnNode(json.getAsInt());
		}
		JsonObject object = json.getAsJsonObject();
		int val = object.get("opcode").getAsInt();
		switch (val)
		{
		case BIPUSH:
		case SIPUSH:
		case NEWARRAY:
			int operand = object.get("operand").getAsInt();
			return new IntInsnNode(val, operand);
		case LDC:
			Object value;
			switch (J.getOrDefault(object, "type", "string"))
			{
			case "int":
				value = object.get("cst").getAsInt();
				break;
			case "long":
				value = object.get("cst").getAsLong();
				break;
			case "float":
				value = object.get("cst").getAsFloat();
				break;
			case "double":
				value = object.get("cst").getAsDouble();
				break;
			case "string":
				value = object.get("cst").getAsString();
				break;
			default:
				throw new RuntimeException("Unknown type LDC node got, type: " + object.get("type").getAsString());
			}
			return new LdcInsnNode(value);
		case ILOAD:
		case LLOAD:
		case FLOAD:
		case DLOAD:
		case ALOAD:
		case ISTORE:
		case LSTORE:
		case FSTORE:
		case ASTORE:
		case RET:
			int var = object.get("var").getAsInt();
			return new VarInsnNode(val, var);
		case IINC:
			int incr = object.get("incr").getAsInt();
			return new IincInsnNode(val, incr);
		case IFEQ:
		case IFNE:
		case IFLT:
		case IFGE:
		case IFGT:
		case IFLE:
		case IF_ICMPEQ:
		case IF_ICMPNE:
		case IF_ICMPLT:
		case IF_ICMPGE:
		case IF_ICMPGT:
		case IF_ICMPLE:
		case IF_ACMPEQ:
		case IF_ACMPNE:
		case GOTO:
		case JSR:
		case IFNULL:
		case IFNONNULL:
			throw new RuntimeException("This node can not used, sorry.");
		case TABLESWITCH:
			throw new RuntimeException("This node can not used, sorry.");
		case LOOKUPSWITCH:
			throw new RuntimeException("This node can not used, sorry.");
		case GETSTATIC:
		case PUTSTATIC:
		case GETFIELD:
		case PUTFIELD:
			String owner = object.get("owner").getAsString();
			String name = object.get("name").getAsString();
			String desc = object.get("desc").getAsString();
			return new FieldInsnNode(val, owner, name, desc);
		case INVOKEVIRTUAL:
		case INVOKESPECIAL:
		case INVOKESTATIC:
		case INVOKEINTERFACE:
			owner = object.get("owner").getAsString();
			name = object.get("name").getAsString();
			desc = object.get("desc").getAsString();
			boolean itf = object.get("itf").getAsBoolean();
			return new MethodInsnNode(val, owner, name, desc, itf);
		case INVOKEDYNAMIC:
			name = object.get("name").getAsString();
			desc = object.get("desc").getAsString();
			throw new RuntimeException("This node can not used, sorry.");
		case NEW:
		case ANEWARRAY:
		case CHECKCAST:
		case INSTANCEOF:
			desc = object.get("desc").getAsString();
			return new TypeInsnNode(val, desc);
		case MULTIANEWARRAY:
			desc = object.get("desc").getAsString();
			int dims = object.get("dims").getAsInt();
			return new MultiANewArrayInsnNode(desc, dims);
		default:
			return new InsnNode(val);
		}
	};
	
	private final Gson gson = new GsonBuilder().registerTypeAdapter(OpInfo.class, this.DESERIALIZER1).registerTypeAdapter(OpLabel.class, this.DESERIALIZER2).registerTypeAdapter(AbstractInsnNode.class, this.DESERIALIZER3).create();
	
	private static File		mcPath;
	private static boolean	runtimeDeobf;
	
	public static File getMcPath()
	{
		return mcPath;
	}
	
	private void extractASMFile(File file, String location, File destination) throws IOException
	{
		if (!file.isDirectory())//zip or jar.
		{
			ZipFile jarFile = new ZipFile(file);
			location += "/";
			if (jarFile.getEntry(location) == null)
			{
				NebulaASMLogHelper.LOG.warn("Asm file does not exist or invalid!");
			}
			else
			{
				Enumeration<? extends ZipEntry> enumeration = jarFile.entries();
				while (enumeration.hasMoreElements())
				{
					ZipEntry entry = enumeration.nextElement();
					if (!entry.isDirectory() && entry.getName().startsWith(location))
					{
						NebulaASMLogHelper.LOG.info("Copy asm data from " + entry.getName());
						FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), new File(destination, entry.getName().substring(location.length())));
					}
				}
			}
			jarFile.close();
		}
		else
		{
			file = new File(file, location);
			if (file.exists())
			{
				NebulaASMLogHelper.LOG.info("Copy asm data from :" + file.getPath());
				FileUtils.copyDirectory(file, destination);
			}
			else
			{
				NebulaASMLogHelper.LOG.warn("Asm file does not exist or invalid!");
			}
		}
	}
	
	private void searchASMFileFromOptional(File file)
	{
		try
		{
			NebulaASMLogHelper.LOG.info("Searching modifications at {}", file.getCanonicalPath());
		}
		catch (IOException exception)
		{
			NebulaASMLogHelper.LOG.warn("Unknown fil path.");
		}
		try
		{
			for (File file2 : file.listFiles(name -> name.getName().endsWith(".json")))
			{
				try (BufferedReader reader = new BufferedReader(new FileReader(file2)))
				{
					OpInfo information = this.gson.fromJson(reader, OpInfo.class);
					information.put();
					NebulaASMLogHelper.LOG.info("Loaded {} modifications.", information.mcpname);
				}
				catch (RuntimeException exception)
				{
					NebulaASMLogHelper.LOG.error("Fail to parse OperationInformation at " + file2.getPath(), exception);
				}
				catch (IOException exception)
				{
					NebulaASMLogHelper.LOG.error("Fail to load OperationInformation", exception);
				}
			}
		}
		catch (RuntimeException exception)
		{
			NebulaASMLogHelper.LOG.error("Failed to searching ASM files.", exception);
		}
	}
	
	private boolean markVersion(File file) throws IOException
	{
		if (file.createNewFile())
		{
			return true;
		}
		else
		{
			try (InputStream stream1 = new BufferedInputStream(new FileInputStream(file)))
			{
				int version = stream1.read();
				if (version != VERSION)
				{
					Frame frame = new Frame();
					return JOptionPane.showConfirmDialog(frame, "" +
							"The Nebula ASM version and your config ASM version are not same.\r\n" +
							"Would you want to update your ASM file? (May causes some game crash\r\n" +
							"when select 'no')",
							"Nebula ASM", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				}
				return false;
			}
			catch (Exception exception)
			{
				NebulaASMLogHelper.LOG.error("Fail to check version file.", exception);
			}
		}
		return true;
	}
	
	@Override
	public Void call() throws Exception
	{
		File destination = new File(mcPath, "asm/" + (runtimeDeobf ? "obf" : "mcp"));
		
		if (!destination.exists())
		{
			if (!destination.mkdirs())
			{
				throw new RuntimeException("Can't create asm file.");
			}
		}
		else if (!destination.isDirectory())
		{
			throw new RuntimeException("Can't read custom asm file.");
		}
		File versionFile = new File(destination, "version.txt");
		if (markVersion(versionFile))
		{
			if (destination.exists())
			{
				FileUtils.cleanDirectory(destination);
			}
			NebulaASMLogHelper.LOG.info("Version file not find or is out date, start ASM file coping.");
			try (OutputStream stream = new FileOutputStream(versionFile))
			{
				stream.write(VERSION);
			}
			catch (Exception exception)
			{
				NebulaASMLogHelper.LOG.error("Fail to add version file.", exception);
			}
			final String suffix = runtimeDeobf ? "obf" : "mcp";
			for (String str : NebulaCoreAPI.ASM_SEARCHING_DIRECTION)
			{
				String targetLocation = "asm/" + str + "/" + suffix;
				try // Insert Operation Files source to location.
				{
					extractASMFile(NebulaLoadingPlugin.source(), targetLocation, destination);
				}
				catch (IOException exception)
				{
					throw new RuntimeException("Fail to extract source files.", exception);
				}
			}
		}
		searchASMFileFromOptional(destination);
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data)
	{
		mcPath = (File) data.get("mcLocation");
		runtimeDeobf = ((Boolean) data.get("runtimeDeobfuscationEnabled")).booleanValue();
	}
}
