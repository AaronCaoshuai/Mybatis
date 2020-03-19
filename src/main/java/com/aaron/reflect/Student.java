package com.aaron.reflect;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生类
 */
@SuppressWarnings("all")
public class Student extends Person implements PersonInterface,Serializable {
    private int grade;
    private Date birthday;
    private Double mark;

    public static final String sex  = "男";
    private static final int   hight = 172;

    public Student() {
    }

    public Student(int grade, Date birthday, Double mark) {
        this.grade = grade;
        this.birthday = birthday;
        this.mark = mark;
    }

    private Student(int grade){
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Student student = (Student) o;

        if (grade != student.grade) return false;
        if (birthday != null ? !birthday.equals(student.birthday) : student.birthday != null) return false;
        if (mark != null ? !mark.equals(student.mark) : student.mark != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + grade;
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (mark != null ? mark.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "grade=" + grade +
                ", birthday=" + birthday +
                ", mark=" + mark +
                '}';
    }

    public static void happy(){
        System.out.println("student happy");
    }

    private String goParty(){
        return "go Party";
    }

    @Override
    public void run(String runName) {
        System.out.println("run " + runName);
    }

    @Override
    public void eat(String footName) {
        System.out.println("eat " + footName);
    }

    @Override
    public String study(String study) {
        return study;
    }

    @Override
    public void sleep() {
        System.out.println("sleep ...");
    }
}
