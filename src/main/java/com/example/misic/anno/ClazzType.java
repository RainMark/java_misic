package com.example.misic.anno;

import javax.lang.model.type.TypeMirror;

import lombok.Builder;
import lombok.Data;

/**
 * @author rain
 */
@Data
@Builder
public class ClazzType {
    private String canonicalName;
    private String simpleName;
    private TypeMirror typeMirror;
}
