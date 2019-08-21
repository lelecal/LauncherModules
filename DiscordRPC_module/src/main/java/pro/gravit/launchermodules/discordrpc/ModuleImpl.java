package pro.gravit.launchermodules.discordrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.jar.JarFile;

import pro.gravit.launcher.modules.Module;
import pro.gravit.launcher.modules.ModuleContext;
import pro.gravit.launchserver.modules.LaunchServerModuleContext;
import pro.gravit.utils.Version;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class ModuleImpl implements Module {
    public static final Version version = new Version(1, 0, 0, 0, Version.Type.LTS);
	public Path config;

    @Override
    public void close() {

    }

    @Override
    public String getName() {
        return "DiscordRPC";
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void init(ModuleContext context1) {
    }

    @Override
    public void preInit(ModuleContext context1) {
    	Config.getOrCreate(config);
    }

    @Override
    public void postInit(ModuleContext context1) {
    	LaunchServerModuleContext context = (LaunchServerModuleContext) context1;
    	try {
			context.launchServer.buildHookManager.registerIncludeClass(DiscordRPC.class.getName(), IOHelper.read(ModuleImpl.class.getResourceAsStream(DiscordRPC.class.getName().replace('.', '/') + ".class")));
			context.launchServer.buildHookManager.registerIncludeClass(Config.class.getName(), IOHelper.read(ModuleImpl.class.getResourceAsStream(Config.class.getName().replace('.', '/') + ".class")));
			context.launchServer.buildHookManager.registerClientModuleClass("pro.gravit.launchermodules.discordrpc.ClientModule");
			context.launchServer.buildHookManager.registerHook(ctx -> {
				try {
					ctx.data.reader.getCp().add(new JarFile(IOHelper.getCodeSource(ModuleImpl.class).toFile()));
			        ctx.output.putNextEntry(IOHelper.newZipEntry("rpc.config.json"));
			        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        Writer w = new OutputStreamWriter(baos, IOHelper.UNICODE_CHARSET);
			        Config.getOrCreate(config).write(w);
			        w.flush();
			        ctx.output.write(baos.toByteArray());
			        ctx.fileList.add("rpc.config.json");
				} catch (IOException e) {
					LogHelper.error(e);
				}
			});
			context.launchServer.buildHookManager.registerIncludeClass("pro.gravit.launchermodules.discordrpc.ClientModule", IOHelper.read(ModuleImpl.class.getResourceAsStream("pro.gravit.launchermodules.discordrpc.ClientModule".replace('.', '/') + ".class")));
    	} catch (IOException e) {
			LogHelper.error(e);
		}
        config = context1.getModulesConfigManager().getModuleConfig(getName());
    }

    public static void main(String[] args) {
        System.err.println("This is module, use with GravitLauncher`s LaunchServer.");
    }
}