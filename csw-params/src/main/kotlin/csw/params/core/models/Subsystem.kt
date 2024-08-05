package csw.params.core.models

/**
 * Represents a TMT subsystem
 *
 * @param description of subsystem
 */
/*
 * Extension function provding a long name
 */
fun Subsystem.longName():String = "$name-$description"

/**
 * Defines constants for the available subsystems
 */
enum class Subsystem(val description: String) {

    AOESW ("Adaptive Optics Executive Software"),
    APS   ("Alignment and Phasing System "),
    CIS   ("Communications and Information Systems"),
    CLN   ("Optical Cleaning Systems"),
    CRYO  ("Instrumentation Cryogenic Cooling System"),
    CSW   ("Common Software"),
    DMS   ("Data Management System"),
    DPS   ("Data Processing System"),
    ENC   ("Enclosure"),
    ESEN  ("Engineering Sensors"),
    ESW   ("Executive Software"),
    HNDL  ("Optics Handling Equipment"),
    HQ    ("Observatory Headquarters"),
    IRIS  ("InfraRed Imaging Spectrometer"),
    LGSF  ("Laser Guide Star Facility"),
    M1COAT("M1COAT M1 Optical Coating System"),
    M1CS  ("M1CS M1 Control System "),
    M1S   ("M1S M1 Optics System"),
    M2COAT("M2/M3 Optical Coating System"),
    M2S   ("M2S M2 System"),
    M3S   ("M3S M3 System"),
    MODHIS("Multi-Object Diffraction-limited High-resolution IR Spectrograph"),
    NFIRAOS("Narrow Field Infrared AO System"),
    OSS   ("Observatory Safety System"),
    REFR  ("Instrumentation Refrigerant Cooling System "),
    SCMS  ("Site Conditions Monitoring System"),
    SER   ("Services"),
    SOSS  ("Science Operations Support Systems"),
    STR   ("Structure "),
    SUM   ("Summit Facilities"),
    TCS   ("Telescope Control System"),
    TINS  ("Test Instruments"),
    WFOS  ("Wide Field Optical Spectrograph"),

    Container("Container Subsystem");

    companion object {
        operator fun invoke(str: String): Subsystem = valueOf(str.uppercase())
    }
}
