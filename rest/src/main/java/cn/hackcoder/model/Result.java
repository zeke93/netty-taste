
package cn.hackcoder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<E> {
    private int code;
    private String msg;
    private E data;

    public static <E> Result<E> success(E data) {
        return new Result<E>(200, "success", data);
    }

    public static <E> Result<E> fail(E data) {
        return new Result<E>(500, "failed", data);
    }

    public static <E> Result<E> fail(int code, String msg) {
        return new Result<E>(code, msg, null);
    }
}
