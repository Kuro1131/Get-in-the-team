package com.min01.getintheteam.client.screen;

import com.min01.getintheteam.Getintheteam;
import com.min01.getintheteam.network.PacketHandler;
import com.min01.getintheteam.network.Sgetteamlist;
import com.min01.getintheteam.network.Skickmember;
import com.min01.getintheteam.network.TeamMemberData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class screen extends Screen {
    private static final Component TITLE = Component.literal("Team Member");
    private static final ResourceLocation Texture = new ResourceLocation(Getintheteam.MODID, "textures/gui/gui.png");
    private List<TeamMemberData> teamMembers;
    private final int imageWidth, imageHeight;
    private boolean isInit = false;
    private int leftPos, topPos;
    private boolean hasTeam;
    private int scrollOffset = 0;
    private static final int ROW_HEIGHT = 48;
    private static final int ROWS_VISIBLE = 5;
    private final Map<String, Entity> entityCache = new HashMap<>();

    public screen(List<TeamMemberData> teamMembers, boolean hasTeam) {
        super(TITLE);
        this.teamMembers = teamMembers;
        this.hasTeam = hasTeam;
        this.imageWidth = 352;
        this.imageHeight = 322;
    }

    public void updateData(List<TeamMemberData> teamMembers, boolean hasTeam) {
        this.teamMembers = teamMembers;
        this.hasTeam = hasTeam;
        this.scrollOffset = Math.min(this.scrollOffset, Math.max(0, teamMembers.size() - 1));
        this.entityCache.clear();
    }

    @Override
    protected void init() {
        super.init();
        isInit = true;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void onClose() {
        isInit = false;
        entityCache.clear();
        super.onClose();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!isInit) {
            onClose();
            return;
        }

        renderBackground(guiGraphics);
        guiGraphics.blit(Texture, this.leftPos, this.topPos, 0, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (!hasTeam) {
            // Show "You are not in a team" message centered in the GUI
            Component message = Component.literal("You are not in a team");
            int textWidth = this.font.width(message);
            guiGraphics.drawString(this.font, message, this.leftPos + (this.imageWidth - textWidth) / 2, this.topPos + (this.imageHeight / 2) - 4, 0xFF5555, false);
            return;
        }

        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + ROWS_VISIBLE, teamMembers.size());

        for (int i = startIndex; i < endIndex; i++) {
            TeamMemberData member = teamMembers.get(i);
            int rowY = this.topPos + 20 + (i - startIndex) * ROW_HEIGHT;
            drawMemberRow(guiGraphics, member, rowY, mouseX, mouseY);
        }

        // Draw scroll indicators if there are more members than visible
        if (teamMembers.size() > ROWS_VISIBLE) {
            if (scrollOffset > 0) {
                guiGraphics.drawString(this.font, "▲", this.leftPos + this.imageWidth - 20, this.topPos + 8, 0xFFFFFF, false);
            }
            if (endIndex < teamMembers.size()) {
                guiGraphics.drawString(this.font, "▼", this.leftPos + this.imageWidth - 20, this.topPos + this.imageHeight - 20, 0xFFFFFF, false);
            }
        }
    }

    private void drawMemberRow(GuiGraphics guiGraphics, TeamMemberData member, int rowY, int mouseX, int mouseY) {
        int rowX = this.leftPos + 10;
        int rowWidth = this.imageWidth - 20;

        // Draw row background (semi-transparent)
        guiGraphics.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT - 4, 0x40000000);

        // Draw entity face picture (head only, clipped to the box)
        Entity entity = getEntityForMember(member);
        if (entity != null) {
            drawEntityFace(guiGraphics, entity, rowX + 22, rowY + ROW_HEIGHT / 2, 32);
        }

        // Draw entity name
        guiGraphics.drawString(this.font, member.getName(), rowX + 48, rowY + 6, 0xFFFFFF, false);

        // Draw entity UUID below the name
        guiGraphics.drawString(this.font, member.getUuid(), rowX + 48, rowY + 20, 0xAAAAAA, false);

        // Draw kick button at top right of the row
        int kickX = rowX + rowWidth - 20;
        int kickY = rowY + 4;
        boolean hovering = mouseX >= kickX && mouseX <= kickX + 16 && mouseY >= kickY && mouseY <= kickY + 16;

        // Kick button background (red)
        guiGraphics.fill(kickX, kickY, kickX + 16, kickY + 16, hovering ? 0xFFFF4444 : 0xFFAA0000);
        // Draw X icon
        guiGraphics.drawString(this.font, "✕", kickX + 4, kickY + 3, 0xFFFFFF, false);
    }

    private Entity getEntityForMember(TeamMemberData member) {
        if (entityCache.containsKey(member.getUuid())) {
            return entityCache.get(member.getUuid());
        }

        Level level = this.minecraft.level;
        if (level == null) return null;

        Entity entity = null;

        // Try to find entity by UUID in the client level
        if (!member.getUuid().isEmpty() && level instanceof ClientLevel clientLevel) {
            try {
                UUID memberUUID = UUID.fromString(member.getUuid());
                for (Entity e : clientLevel.entitiesForRendering()) {
                    if (e.getUUID().equals(memberUUID)) {
                        entity = e;
                        break;
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        // If not found, try to find player by name
        if (entity == null && !member.getName().isEmpty()) {
            for (Player player : level.players()) {
                if (player.getName().getString().equals(member.getName())) {
                    entity = player;
                    break;
                }
            }
        }

        // Fall back to creating a dummy entity from the entity type
        if (entity == null && !member.getEntityType().isEmpty()) {
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                if (type.getDescriptionId().equals(member.getEntityType())) {
                    entity = type.create(level);
                    break;
                }
            }
        }

        entityCache.put(member.getUuid(), entity);
        return entity;
    }

    private void drawEntityFace(GuiGraphics guiGraphics, Entity entity, int centerX, int centerY, int boxSize) {
        int half = boxSize / 2;

        // Clip rendering to the face box so only the head is visible
        guiGraphics.enableScissor(centerX - half, centerY - half, centerX + half, centerY + half);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        float scale = 32.0F; // pixels per block

        // Anchor at the box center, shifted down so the entity's head lands in the box
        // (z=1050 like vanilla inventory - after the -1000 flip shift the model sits in front of the GUI)
        poseStack.translate(centerX, centerY, 1050.0F);
        poseStack.translate(0.0F, entity.getEyeHeight() * scale, 0.0F);

        // Vanilla inventory-style transform: keeps the entity right-side up and facing the viewer
        poseStack.scale(1.0F, 1.0F, -1.0F);
        poseStack.translate(0.0F, 0.0F, 1000.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(scale, scale, scale);

        // Rotate the entity to look straight at the viewer
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = 180.0F;
            livingEntity.setYRot(180.0F);
            livingEntity.yHeadRot = 180.0F;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        dispatcher.overrideCameraOrientation(new Quaternionf());
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, bufferSource, 0xF000F0);
        dispatcher.setRenderShadow(true);
        bufferSource.endBatch();

        poseStack.popPose();
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hasTeam) {
            int startIndex = scrollOffset;
            int endIndex = Math.min(startIndex + ROWS_VISIBLE, teamMembers.size());

            // Check kick buttons
            for (int i = startIndex; i < endIndex; i++) {
                TeamMemberData member = teamMembers.get(i);
                int rowY = this.topPos + 20 + (i - startIndex) * ROW_HEIGHT;
                int rowX = this.leftPos + 10;
                int rowWidth = this.imageWidth - 20;

                int kickX = rowX + rowWidth - 20;
                int kickY = rowY + 4;

                if (mouseX >= kickX && mouseX <= kickX + 16 && mouseY >= kickY && mouseY <= kickY + 16) {
                    // Kick member from team - use UUID if available, otherwise use player name
                    String memberId = !member.getUuid().isEmpty() ? member.getUuid() : member.getName();
                    if (!memberId.isEmpty()) {
                        PacketHandler.sendToServer(new Skickmember(memberId));
                        // Refresh the team list after kicking
                        PacketHandler.sendToServer(new Sgetteamlist());
                    }
                    return true;
                }
            }

            // Check scroll arrow clicks
            if (scrollOffset > 0 && mouseX >= this.leftPos + this.imageWidth - 20 && mouseX <= this.leftPos + this.imageWidth - 4 && mouseY >= this.topPos + 8 && mouseY <= this.topPos + 20) {
                scrollOffset--;
                return true;
            }
            if (endIndex < teamMembers.size() && mouseX >= this.leftPos + this.imageWidth - 20 && mouseX <= this.leftPos + this.imageWidth - 4 && mouseY >= this.topPos + this.imageHeight - 20 && mouseY <= this.topPos + this.imageHeight - 8) {
                scrollOffset++;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (hasTeam && teamMembers.size() > ROWS_VISIBLE) {
            if (delta > 0 && scrollOffset > 0) {
                scrollOffset--;
            } else if (delta < 0 && scrollOffset < teamMembers.size() - ROWS_VISIBLE) {
                scrollOffset++;
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}