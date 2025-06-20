Rest API quản lý nguời dùng với các chức năng:
- Đăng kí, đăng nhập (JWT)
- Phân quyền ADMIN, USER
- Xóa mềm người dùng
- Xem thông tin chi tiết toàn bộ và thông tin chi tiết một người dùng
- Tìm kiếm thông tin theo id, tên và email
- Cập nhật thông tin người dùng
Công nghệ:
- Visual Studio Code
- Java 17
- Spring Boot
- PostgreSQL
- Postman
- Lombok
- Docker
- Docker Hub image:(https://hub.docker.com/repository/docker/nguyenhuonggiang/baitestfresher-app/general)

Cài Đặt & Chạy Ứng Dụng
-  [Docker](https://www.docker.com)
-  [Git](https://git-scm.com)
Build & chạy service
docker-compose up --build

Chạy trên postman
http://localhost:8080/api/users/registerUser 
Với form-data
key: user (text)
value:
{
   "name": "Giang",
   "userName": "nguyengiang",
   "passWord": "123456",
   "email": "giang.nguyenhuong1508@gmail.com",
   "phoneNumber": "0325975732",
   "type": "user"
 }
 key: avatar (File) 
 value: chọn ảnh

http://localhost:8080/api/users/loginUser  // đăng nhập với username và password
http://localhost:8080/api/users/logout
http://localhost:8080/api/users/getAllUsers // chỉ có admin mới có thể xem
http://localhost:8080/api/users/getUserId/5 với 5 là id người dùng
http://localhost:8080/api/users/deleteUser/5 với 5 là id người dùng và chỉ admin mới có quyền xóa, sau khi xóa sẽ từ 0 -> 1 với 0 là trạng thái chưa bị xóa hay vô hiệu hóa
http://localhost:8080/api/users/getAllUsers?keyword=nguyengiang // keyword có thể là id hoặc username hoặc email
http://localhost:8080/api/users/updateUser/5 //  Json
{
   "name": "Nguyễn Hương Giang",
   "userName": "giang123",
   "email": "giang@gmail.com",
   "phoneNumber": "0987654321",
   "avatar": "https://example.com/avatar.jpg",
   "status": "ACTIVE"
}


