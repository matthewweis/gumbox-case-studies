package tc

object CoolingFan_and_TempControl_Demo {

  def main(args: Array[String]): Unit = {
    art.Art.run(
      CoolingFan_and_TempControl_Arch.ad,
      CoolingFan_and_TempControl_Arch.services,
      art.scheduling.legacy.Legacy(CoolingFan_and_TempControl_Arch.ad.components)
    )
  }

}
