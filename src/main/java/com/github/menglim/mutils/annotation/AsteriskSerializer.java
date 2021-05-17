package com.github.menglim.mutils.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class AsteriskSerializer extends StdSerializer<Object> implements ContextualSerializer {

    String asterisk;
    int showLastDigit;
    int showFirstDigit;

    public AsteriskSerializer() {
        super(Object.class);
    }

    public AsteriskSerializer(String asterisk, int showFirstDigit, int showLastDigit) {
        super(Object.class);
        this.asterisk = asterisk;
        this.showFirstDigit = showFirstDigit;
        this.showLastDigit = showLastDigit;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty property) {
        Optional<Asterisk> anno = Optional.ofNullable(property)
                .map(prop -> prop.getAnnotation(Asterisk.class));
        return new AsteriskSerializer(anno.map(Asterisk::value).orElse(null), anno.map(Asterisk::showFirstDigit).orElse(2), anno.map(Asterisk::showLastDigit).orElse(2));
    }

    @Override
    public void serialize(Object obj, JsonGenerator gen, SerializerProvider prov) throws IOException {
        if (obj != null) {
            String val = obj.toString();
            String first = StringUtils.left(val, showFirstDigit);
            String last = StringUtils.right(val, showLastDigit);
            gen.writeString(first + asterisk + last);
        } else {
            gen.writeString(asterisk);
        }
    }
}
