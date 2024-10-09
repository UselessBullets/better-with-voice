package jakraes.betterwithvoice.mixins;

import jakraes.betterwithvoice.interfaces.IGameSettingsMixin;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GameSettings.class, remap = false)
public class GameSettingsMixin implements IGameSettingsMixin {
	@Unique
	public KeyBinding keyActivateVoice = new KeyBinding("key.betterwithvoice.activateVoice").bind(InputDevice.keyboard, Keyboard.KEY_V);

	@Override
	public KeyBinding betterwithvoice$getActivateVoiceKey() {
		return keyActivateVoice;
	}
}
