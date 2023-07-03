import { Button, Select } from 'react-daisyui';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import { useTable, usePagination } from 'react-table';

function Table({ columns, data }) {
  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    prepareRow,
    page, // Instead of using 'rows', we'll use page,
    // which has only the rows for the active page

    // The rest of these things are super handy, too ;)
    canPreviousPage,
    canNextPage,
    pageOptions,
    nextPage,
    previousPage,
    setPageSize,
    state: { pageIndex, pageSize }
  } = useTable(
    {
      columns,
      data,
      initialState: { pageIndex: 0, pageSize: 10 }
    },
    usePagination
  );

  return (
    <>
      <table className="table w-full mb-8" {...getTableProps()}>
        <thead>
          {headerGroups.map((headerGroup) => (
            <tr {...headerGroup.getHeaderGroupProps()}>
              {headerGroup.headers.map((column) => (
                <th
                  {...column.getHeaderProps({
                    style: {
                      maxWidth: column.maxWidth
                    }
                  })}
                >
                  {column.render('Header')}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody {...getTableBodyProps()}>
          {page.map((row, i) => {
            prepareRow(row);
            return (
              <tr {...row.getRowProps()}>
                {row.cells.map((cell) => {
                  return (
                    <td
                      {...cell.getCellProps({
                        style: {
                          maxWidth: cell.column.maxWidth
                        }
                      })}
                    >
                      {cell.render('Cell')}
                    </td>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
      <div className="pagination flex items-center">
        <div className="pr-5">
          <Button onClick={() => previousPage()} disabled={!canPreviousPage}>
            <FaChevronLeft />
          </Button>{' '}
          <Button onClick={() => nextPage()} disabled={!canNextPage}>
            <FaChevronRight />
          </Button>
        </div>
        <div className="pr-5">
          Strona{' '}
          <strong>
            {pageIndex + 1} z {pageOptions.length}
          </strong>{' '}
        </div>
        <div className="pr-5">
          <Select
            value={pageSize}
            onChange={(e) => {
              setPageSize(Number(e.target.value));
            }}
          >
            {[10, 25, 50].map((pageSize) => (
              <Select.Option key={pageSize} value={pageSize}>
                Pokaż {pageSize}
              </Select.Option>
            ))}
          </Select>
        </div>
        <div className="pr-5 ml-auto">
          Ilość wyników: <strong>{data.length}</strong>
        </div>
      </div>
    </>
  );
}

export default Table;
