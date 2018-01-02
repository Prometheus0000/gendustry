/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.gendustry.gui.BlockGuiWrenchable
import net.bdew.gendustry.machines.MachineMaterial
import net.bdew.gendustry.misc.BlockTooltipHelper
import net.bdew.lib.block.{BaseBlock, BlockKeepData, BlockTooltip, HasTE}
import net.bdew.lib.covers.BlockCoverable
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}

object BlockApiary extends BaseBlock("industrial_apiary", MachineMaterial) with HasTE[TileApiary] with BlockCoverable with BlockGuiWrenchable with BlockTooltip with BlockKeepData {
  val TEClass = classOf[TileApiary]
  lazy val guiId: Int = MachineApiary.guiId

  setHardness(2)

  override def getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int = {
    if (state.getBlock != this)
      return state.getBlock.getLightValue(state)
    else if (world.getBlockState(pos).getBlock != this)
      return 0
    else if (world.getTileEntity(pos) != null && getTE(world, pos).exists(_.hasLight))
      return 15
    else
      return 0
  }

  override def getTooltip(stack: ItemStack, world: World, flags: ITooltipFlag): List[String] = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey("data")) {
      val data = stack.getTagCompound.getCompoundTag("data")
      val inv = BlockTooltipHelper.getInventory(data)

      List.empty ++
        (inv.get(0) map (_.getDisplayName)) ++
        (inv.get(1) map (_.getDisplayName)) ++
        BlockTooltipHelper.getPowerTooltip(data, "power") ++
        BlockTooltipHelper.getItemsTooltip(data)

    } else List.empty
  }

  override def restoreTileEntity(world: World, pos: BlockPos, is: ItemStack, player: EntityPlayer): Unit = {
    super.restoreTileEntity(world, pos, is, player)
    if (player.isInstanceOf[EntityPlayerMP])
      getTE(world, pos).owner := player.asInstanceOf[EntityPlayerMP].getGameProfile
  }

  override def canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean = true
}
