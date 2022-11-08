import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure


@OptIn( ExperimentalSerializationApi::class )
class PolymorphicPrimitiveSerializer<T> (val typeSerializer: KSerializer<T>) : KSerializer<T>
{
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor( typeSerializer.descriptor.serialName )
    {
        element( "value", typeSerializer.descriptor )
    }
    override fun deserialize( decoder: Decoder): T =


        decoder.decodeStructure( descriptor )
        {
            decodeElementIndex( descriptor )
            decodeSerializableElement( descriptor, 0,typeSerializer)
        }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure( descriptor )
        {
            encodeSerializableElement( descriptor, 0, typeSerializer, value )
        }
    }
}

//usage example
val mmySerializationModule = SerializersModule {
    polymorphic(Any::class) {
        subclass(Int::class, PolymorphicPrimitiveSerializer(Int.serializer()))
        subclass(String::class, PolymorphicPrimitiveSerializer(String.serializer()))
        subclass(Boolean::class, PolymorphicPrimitiveSerializer(Boolean.serializer()))
   }}
