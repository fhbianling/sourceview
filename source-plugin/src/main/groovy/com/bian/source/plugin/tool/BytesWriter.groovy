package com.bian.source.plugin.tool

class BytesWriter {

    static byte[] assembleAssetBytes(byte[] fileBytes, byte[] jsonBytes) {
        def paddedLength = getPaddedLength(jsonBytes.length)
        def dataBytes = new byte[8 + paddedLength + fileBytes.length]
        copyIntToBytes(dataBytes, 0, jsonBytes.length)
        copyIntToBytes(dataBytes, 4, paddedLength)
        System.arraycopy(jsonBytes, 0, dataBytes, 8, jsonBytes.length)
        System.arraycopy(fileBytes, 0, dataBytes, 8 + paddedLength, fileBytes.length)
        dataBytes
    }

    static void writeBytesToFile(File dst, byte[] dataBytes) {
        if (!dst.parentFile.exists()) {
            dst.parentFile.mkdirs()
        }
        if (!dst.exists()) {
            dst.createNewFile()
        }
        dst.withOutputStream {
            it.write(dataBytes)
        }
    }

    static int getPaddedLength(long length) {
        if (length < 4) return 4
        return length - 1 + (4 - (length - 1) % 4)
    }


    static void copyIntToBytes(byte[] bytes, int start, int value) {
        bytes[start] = (value >> 24) & 0xFF
        bytes[start + 1] = (value >> 16) & 0xFF
        bytes[start + 2] = (value >> 8) & 0xFF
        bytes[start + 3] = value & 0xFF
    }
}