/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.realtime.mixin;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.mixin.realtime.IMixinMinecraftServer;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    private static final String ENTITY_PLAYER_XP_COOLDOWN_FIELD = "Lnet/minecraft/entity/player/EntityPlayer;xpCooldown:I";
    private static final String ENTITY_PLAYER_SLEEP_TIMER_FIELD = "Lnet/minecraft/entity/player/EntityPlayer;sleepTimer:I";
    @Shadow public int xpCooldown;
    @Shadow private int sleepTimer;

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = ENTITY_PLAYER_XP_COOLDOWN_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupXpCooldown(EntityPlayer self, int modifier) {
        int ticks = (int) ((IMixinMinecraftServer) self.getEntityWorld().getMinecraftServer()).getRealTimeTicks();
        this.xpCooldown = Math.max(0, this.xpCooldown - ticks);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = ENTITY_PLAYER_SLEEP_TIMER_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupSleepTimer(EntityPlayer self, int modifier) {
        int ticks = (int) ((IMixinMinecraftServer) self.getEntityWorld().getMinecraftServer()).getRealTimeTicks();
        this.sleepTimer += ticks;
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = ENTITY_PLAYER_SLEEP_TIMER_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 2))
    public void fixupWakeTimer(EntityPlayer self, int modifier) {
        int ticks = (int) ((IMixinMinecraftServer) self.getEntityWorld().getMinecraftServer()).getRealTimeTicks();
        this.sleepTimer += ticks;
    }

}
