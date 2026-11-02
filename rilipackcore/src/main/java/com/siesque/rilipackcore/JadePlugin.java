package com.siesque.rilipackcore;

import com.siesque.rilipackcore.block.entity.multiblock.CleanroomControllerEntity;
import com.siesque.rilipackcore.block.entity.multiblock.EnergyInputHatchEntity;
import com.siesque.rilipackcore.block.multiblock.CleanroomControllerBlock;
import com.siesque.rilipackcore.block.multiblock.EnergyInputHatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    public static final ResourceLocation CLEANROOM_INFO = Rilipackcore.rl("cleanroom_info");
    public static final ResourceLocation ENERGY_HATCH_INFO = Rilipackcore.rl("energy_hatch_info");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CleanroomProvider.INSTANCE, CleanroomControllerEntity.class);
        registration.registerBlockDataProvider(EnergyHatchProvider.INSTANCE, EnergyInputHatchEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CleanroomProvider.INSTANCE, CleanroomControllerBlock.class);
        registration.registerBlockComponent(EnergyHatchProvider.INSTANCE, EnergyInputHatch.class);
    }

    public enum CleanroomProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getServerData().contains("DisplayText")) {
                CompoundTag tag = accessor.getServerData().getCompound("DisplayText");
                int count = tag.getInt("Count");
                for (int i = 0; i < count; i++) {
                    tooltip.add(Component.Serializer.fromJson(tag.getString("Text" + i)));
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof CleanroomControllerEntity controllerEntity) {
                java.util.List<Component> text = controllerEntity.getDisplayText();
                CompoundTag tag = new CompoundTag();
                tag.putInt("Count", text.size());
                for (int i = 0; i < text.size(); i++) {
                    tag.putString("Text" + i, Component.Serializer.toJson(text.get(i)));
                }
                data.put("DisplayText", tag);
            }
        }

        @Override
        public ResourceLocation getUid() {
            return CLEANROOM_INFO;
        }
    }

    public enum EnergyHatchProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getServerData().contains("Energy")) {
                CompoundTag energyTag = accessor.getServerData().getCompound("Energy");
                long stored = energyTag.getLong("Stored");
                long capacity = energyTag.getLong("Capacity");
                tooltip.add(Component.translatable("rilipackcore.jade.energy", stored, capacity));
            }
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            accessor.getBlockEntity().getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
                CompoundTag energyTag = new CompoundTag();
                energyTag.putLong("Stored", energy.getEnergyStored());
                energyTag.putLong("Capacity", energy.getMaxEnergyStored());
                data.put("Energy", energyTag);
            });
        }

        @Override
        public ResourceLocation getUid() {
            return ENERGY_HATCH_INFO;
        }
    }
}