# 一、知识点补充
#### 1.序列化和持久化的区别
# 二、Java中的序列化和反序列化
Java中相关接口包括**Serializable**接口和**Externalizable**接口。<br>其中Externalizable接口继承自Serializable接口并新增了writerExternal(ObjectOuput)和readExternal(ObjectInput)两个方法。<br>Serializable接口和Externalizable接口都需要配合ObjectOutputStream和ObjectInputStream两个流进行对象的序列化和反序列化。<br>注意，如下2个步骤均已省略递归的读和写，因为一个Serializable接口的实现类，其包含的字段也有可能也是Serializable的。
## 1.序列化
当通过ObjectOutputStream序列化一个对象时：
1. [**writeOrdinaryObject**]<br>如果判断是写入对象，则首先写入TC_OBJECT标志位。
2. [**writeExternalData**]<br>如果判断该对象为Externalizable接口的实现类，则会调用writeExternal进行序列化。
3. [**writeSerialData**]<br>如果判断该对象为Serializable接口的实现类,并且通过反射判断其包含非静态私有writeObject方法，则会通过反射调用其writeObject方法来进行序列化。
4. [**writeSerialData**->**defaultWriteFields**]<br>如果判断该对象为Serializable接口的实现类，但不包含writeObject方法，则会通过反射获取其字段信息，并进行序列化。

需要注意的是，为了确保尽可能少的反射调用，ObjectOutputStream通过ObjectStreamClass来缓存相关的反射信息。
## 2.反序列化
当通过ObjectInputStream反序列化一个对象时：
1. [**readObject0**]<br>首先读取TC标记位，判断是否为对象。如果是对象，则开始读取。
2. [**readOrdinaryObject**->**readClassDesc**]<br>从byte数组中读取ObjectStreamClass。
3. [**readOrdinaryObject**->**isInstantiable**?**ObjectStreamClass.newInstance**] <br>如果判断ObjectStreamClass已经初始化，则调用构造器创建一个ObjectStreamClass对应的空对象（注意这里并非调用对应类的构造，多半是调用Object的构造）。
4. [**readExternalData**]<br>如果类是Externalizable的，则调用readExternal使其从ObjectInput中进行反序列化的字段赋值。
5. [**readSerialData**]<br>如果类是Serializable的，则同样首先判断是否有readObject，否则直接通过反射进行字段赋值。
##### 为什么创建对象时调用的并非对应类本身的构造？
ObjectStreamClass通过ReflectionFactory的如下方法确认newInstance时所需的对象构造函数：
```
public final Constructor<?> newConstructorForSerialization(Class<?> cl) {
    Class initCl = cl;

    Class prev;
    do {
        if (!Serializable.class.isAssignableFrom(initCl)) {
            Constructor constructorToCall;
            try {
                constructorToCall = initCl.getDeclaredConstructor();
                int mods = constructorToCall.getModifiers();
                if ((mods & 2) != 0 || (mods & 5) == 0 && !packageEquals(cl, initCl)) {
                    return null;
                }
            } catch (NoSuchMethodException var5) {
                return null;
            }

            return this.generateConstructor(cl, constructorToCall);
        }

        prev = initCl;
    } while((initCl = initCl.getSuperclass()) != null && (disableSerialConstructorChecks || this.superHasAccessibleConstructor(prev)));

    return null;
}
```
也即是说，所调用的是类的第一个非Serializable的间接父类的构造函数。
可以通过如下代码验证：
```
public static class NonSerial {
    public NonSerial() {
        System.out.println("a");
    }
}

public static class Pojo extends NonSerial implements Serializable {
    private String name;
    private int value;
    private final static long serialVersionUID = -1;

    public Pojo() {
        System.out.println("b");
    }

    public Pojo(String name, int value) {
        this.name = name;
        this.value = value;
        System.out.println("c");
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
```
当反序列化一个Pojo对象时，会打印”a“,但不会打印"b"或"c"。因此假如Pojo没有继承自NonSerial而是直接实现自Serializable，则此时会调用Object的构造函数。
# 三、Android中的序列化和反序列化
Parcelable->Binder
Parcel.cpp
# 四、对比

|Serializable|Parcelable|
|------------|----------|
|通过IO对硬盘操作，速度较慢|直接在内存操作，效率高，性能好|
|大小不受限制|一般不能超过1M，修改内核也只能4M（Binder）|
|大量使用反射，产生内存碎片 ||
