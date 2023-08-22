drop procedure if exists insert_sales_transactions ;

delimiter //
CREATE PROCEDURE insert_sales_transactions
(
    IN pEmail varchar(50),
    IN pMovieId varchar(50),
    IN pQuantity INT,
    IN pSaleDate date,
    IN pToken varchar(50)
)
BEGIN
  insert into sales (email, movieId, quantity, saleDate) values
     (pEmail, pMovieId, pQuantity, pSaleDate);

  select @pSId := last_insert_id();
  insert into transactions (sId, token) values
    (@pSId, pToken);

END//
delimiter ;
