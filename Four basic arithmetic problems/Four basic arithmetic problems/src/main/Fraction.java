package main;

import java.math.BigInteger;

/**
 * 分数类，支持自然数和真分数的表示与运算
 * 格式：自然数3表示为3/1；真分数3/5；带分数2'3/8
 */
public class Fraction {
    private long numerator;   // 分子
    private long denominator; // 分母

    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("分母不能为0");
        }
        // 统一符号，分母恒正
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        this.numerator = numerator;
        this.denominator = denominator;
        simplify();
    }

    // 自然数构造
    public Fraction(long number) {
        this(number, 1);
    }

    // 约分
    private void simplify() {
        if (numerator == 0) {
            denominator = 1;
            return;
        }
        long gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= gcd;
        denominator /= gcd;
    }

    private long gcd(long a, long b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValue();
    }

    // 加法
    public Fraction add(Fraction other) {
        long num = this.numerator * other.denominator + other.numerator * this.denominator;
        long den = this.denominator * other.denominator;
        return new Fraction(num, den);
    }

    // 减法
    public Fraction subtract(Fraction other) {
        long num = this.numerator * other.denominator - other.numerator * this.denominator;
        long den = this.denominator * other.denominator;
        return new Fraction(num, den);
    }

    // 乘法
    public Fraction multiply(Fraction other) {
        long num = this.numerator * other.numerator;
        long den = this.denominator * other.denominator;
        return new Fraction(num, den);
    }

    // 除法
    public Fraction divide(Fraction other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("除数不能为0");
        }
        long num = this.numerator * other.denominator;
        long den = this.denominator * other.numerator;
        return new Fraction(num, den);
    }

    // 比较大小：this >= other 返回true
    public boolean isGreaterOrEqual(Fraction other) {
        return this.numerator * other.denominator >= other.numerator * this.denominator;
    }

    // 判断是否为真分数（分子 < 分母，且为正数）
    public boolean isProperFraction() {
        return numerator > 0 && numerator < denominator;
    }

    // 判断是否为自然数
    public boolean isNaturalNumber() {
        return denominator == 1;
    }

    // 转为输出格式字符串
    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }
        if (Math.abs(numerator) < denominator) {
            return numerator + "/" + denominator;
        }
        // 带分数
        long integer = numerator / denominator;
        long remain = Math.abs(numerator % denominator);
        if (remain == 0) {
            return String.valueOf(integer);
        }
        return integer + "'" + remain + "/" + denominator;
    }

    // 用于去重的规范化字符串（不考虑加法乘法交换律）
    public String toNormalizedString() {
        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fraction)) return false;
        Fraction fraction = (Fraction) o;
        return numerator == fraction.numerator && denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        return (int) (31 * numerator + denominator);
    }

    // getters
    public long getNumerator() { return numerator; }
    public long getDenominator() { return denominator; }
}
