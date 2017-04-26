package com.barswipe.model;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by Soli on 2017/1/10.
 */

public class DataBaseManager {
    private Student mStudent;

    /**
     * 插入一个学生信息
     *
     * @param name 姓名
     * @param age  年龄
     */
    public void insertData(String name, int age) {
        mStudent = queryData(name, age);
        if (mStudent == null) {
            mStudent = new Student();
            mStudent.name = name;
            mStudent.age = age;
        }
        mStudent.time = System.currentTimeMillis();
        mStudent.save();//如果插入数据已存在,则更新原数据
    }

    /**
     * 查询所有的学生
     *
     * @param
     * @return
     */
    public List<Student> getData() {
        List<Student> record = SQLite.select()
                .from(Student.class)
                .orderBy(Student_Table.time, false)
                .queryList();
        return record;
    }

    /**
     * 删除学生信息
     *
     * @param
     */
    public void deletAllData(String name, int age) {
        List<Student> record = SQLite.select()
                .from(Student.class)
                .where(Student_Table.name.eq(name))
                .and(Student_Table.age.eq(age))

                .queryList();
        for (Student student : record) {
            student.delete();
        }
    }

    /**
     * @param name
     * @param age
     * @return
     */
    public Student queryData(String name, int age) {
        Student record = SQLite.select()
                .from(Student.class)
                .where(Student_Table.name.eq(name))
                .and(Student_Table.age.eq(age))
                .querySingle();
        return record;
    }
}
