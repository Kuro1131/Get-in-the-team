package com.min01.getintheteam.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Pose;

public class HealedPacket extends Packet {
    private int entityId;
    private float health;
    private Pose pose;

    public HealedPacket(int entityId, float health, Pose pose) {
        this.entityId = entityId;
        this.health = health;
        this.pose = pose;
    }

    public void readBytes(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        health = buf.readFloat();
        pose = buf.readEnum(Pose.class);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(health);
        buf.writeEnum(pose);
    }

    @Override
    public void handle(PacketListener pHandler) {

    }

}
