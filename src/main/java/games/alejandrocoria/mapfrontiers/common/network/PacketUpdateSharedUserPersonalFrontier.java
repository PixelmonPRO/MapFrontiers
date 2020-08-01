package games.alejandrocoria.mapfrontiers.common.network;

import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

import games.alejandrocoria.mapfrontiers.MapFrontiers;
import games.alejandrocoria.mapfrontiers.common.FrontierData;
import games.alejandrocoria.mapfrontiers.common.FrontiersManager;
import games.alejandrocoria.mapfrontiers.common.settings.FrontierSettings;
import games.alejandrocoria.mapfrontiers.common.settings.SettingsUser;
import games.alejandrocoria.mapfrontiers.common.settings.SettingsUserShared;
import games.alejandrocoria.mapfrontiers.common.util.UUIDHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@ParametersAreNonnullByDefault
public class PacketUpdateSharedUserPersonalFrontier implements IMessage {
    private UUID frontierID;
    private SettingsUserShared userShared;

    public PacketUpdateSharedUserPersonalFrontier() {
        userShared = new SettingsUserShared();
    }

    public PacketUpdateSharedUserPersonalFrontier(UUID frontierID, SettingsUserShared user) {
        this.frontierID = frontierID;
        userShared = user;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        frontierID = UUIDHelper.fromBytes(buf);
        userShared.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        UUIDHelper.toBytes(buf, frontierID);
        userShared.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketUpdateSharedUserPersonalFrontier, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateSharedUserPersonalFrontier message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    SettingsUser playerUser = new SettingsUser(player);

                    FrontierData currentFrontier = FrontiersManager.instance.getFrontierFromID(message.frontierID);

                    if (currentFrontier != null && currentFrontier.getPersonal()) {
                        if (FrontiersManager.instance.getSettings().checkAction(FrontierSettings.Action.PersonalFrontier,
                                playerUser, MapFrontiers.proxy.isOPorHost(player), currentFrontier.getOwner())) {
                            SettingsUserShared currentUserShared = currentFrontier.getUserShared(message.userShared.getUser());

                            if (currentUserShared == null) {
                                return;
                            }

                            currentUserShared.setActions(message.userShared.getActions());
                            currentFrontier.addChange(FrontierData.Change.Shared);

                            PacketHandler.sendToUsersWithAccess(
                                    new PacketFrontierUpdated(currentFrontier, ctx.getServerHandler().player.getEntityId()),
                                    currentFrontier);
                        } else {
                            PacketHandler.INSTANCE.sendTo(
                                    new PacketSettingsProfile(FrontiersManager.instance.getSettings().getProfile(player)),
                                    player);
                        }
                    }
                });
            }

            return null;
        }
    }
}
