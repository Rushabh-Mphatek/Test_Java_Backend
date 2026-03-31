Springboot 2 with mysql demo

```bash
Create mysql table

CREATE TABLE students(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	height FLOAT NULL,
	PRIMARY KEY (id)
) engine = InnoDB

body.txt

{
    "name": "Dylan Ngo",
    "height": 120
}

ab -p body.txt -T application/json -c 100 -n 10000 http://localhost:8080/student/add


```