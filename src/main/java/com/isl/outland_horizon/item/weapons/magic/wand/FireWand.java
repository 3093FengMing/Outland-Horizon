package com.isl.outland_horizon.item.weapons.magic.wand;

import com.isl.outland_horizon.item.weapons.weapon.AbstractWeapon;
import com.isl.outland_horizon.utils.ManaUtils;
import com.isl.outland_horizon.utils.Utils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class FireWand extends AbstractWeapon implements GeoItem {
    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("fire_wand.loop");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FireWand() {
        super(300,0,10, Items.DIAMOND);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FireWandRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new FireWandRenderer();
                }

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.STOP)
                .triggerableAnim("activate", ACTIVATE_ANIM));
    }
    @Override
    public void whenInInventory(Entity entity, ItemStack itemStack, int slotId, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            triggerAnim(entity, GeoItem.getOrAssignId(itemStack, serverLevel), "Activation", "activate");
        }
        super.whenInInventory(entity, itemStack, slotId, level);
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, InteractionHand interactionHand) {
        if(ManaUtils.removeMana(player, 10)){
            Vec3 lookVec = extendVector(player.getLookAngle(),10);
            Vec3 posVec = player.position();
            SmallFireball smallFireball = new SmallFireball(level, posVec.x, posVec.y + 1, posVec.z, lookVec.x, lookVec.y, lookVec.z);
            smallFireball.setOwner(player);
            level.addFreshEntity(smallFireball);
            player.getCooldowns().addCooldown(this, 20);
            Utils.sendSimpleMessageToPlayer(player, String.valueOf(ManaUtils.getMana(player)));
        }
        return super.use(level, player, interactionHand);
    }
    public static Vec3 extendVector(Vec3 originalVector, double scaleFactor) {
        double newX = originalVector.x * scaleFactor;
        double newY = originalVector.y * scaleFactor;
        double newZ = originalVector.z * scaleFactor;
        return new Vec3(newX, newY, newZ);
    }
    @Override
    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull Level p_41405_, @NotNull Entity p_41406_, int p_41407_, boolean p_41408_) {
        if (p_41405_ instanceof ServerLevel serverLevel) {
            triggerAnim(p_41406_, GeoItem.getOrAssignId(p_41404_, serverLevel), "Activation", "activate");
        }
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
