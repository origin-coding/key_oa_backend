package oa.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import oa.annotation.Desensitize
import oa.annotation.Desensitize.DesensitizeStrategy
import org.springframework.security.core.context.SecurityContextHolder

class DesensitizeJsonSerializer : JsonSerializer<String>(), ContextualSerializer {
    private lateinit var strategy: DesensitizeStrategy

    override fun serialize(value: String, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(strategy.desensitizeSerializer().apply(value))
    }

    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        // 如果具有修改的权限，那么不对数据进行脱敏，直接返回结果
        val authorities = SecurityContextHolder.getContext().authentication.authorities.map { it.authority }
        if ("oa:employee:modify" in authorities) {
            return prov.findValueSerializer(property.type, property)
        }

        val annotation: Desensitize? = property.getAnnotation(Desensitize::class.java)
        if (annotation !== null && property.type.rawClass == String::class.java) {
            this.strategy = annotation.strategy
            return this
        }
        return prov.findValueSerializer(property.type, property)
    }
}