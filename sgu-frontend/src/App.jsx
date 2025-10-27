import React, { useMemo, useState } from 'react';
import './index.css';
import { userService } from './services/api';

export default function App() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isOpen, setIsOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const emptyForm = useMemo(
    () => ({ name: '', lastname: '', surname: '', email: '', tel: '' }),
    []
  );
  const [form, setForm] = useState(emptyForm);

  const openAdd = () => {
    setEditingId(null);
    setForm(emptyForm);
    setIsOpen(true);
  };

  const openEdit = (user) => {
    setEditingId(user.id);
    setForm({
      name: user.name,
      lastname: user.lastname,
      surname: user.surname,
      email: user.email,
      tel: user.tel,
    });
    setIsOpen(true);
  };

  const close = () => {
    setIsOpen(false);
    setForm(emptyForm);
    setEditingId(null);
  };

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const onSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const action = editingId
      ? userService.update(editingId, form)
      : userService.create(form);

    action
      .then(() => {
        close();
        loadUsers();
      })
      .catch((err) => {
        setError(err.message);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const onDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este usuario?')) return;
    
    setLoading(true);
    setError(null);
    try {
      await userService.delete(id);
      loadUsers();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await userService.getAll();
      const list = Array.isArray(data)
        ? data
        : Array.isArray(data?.data)
        ? data.data
        : Array.isArray(data?.users)
        ? data.users
        : [];
      setUsers(list);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    loadUsers();
  }, []);

  return (
    <div className="min-h-screen bg-gray-50 text-gray-900">
      <div className="max-w-6xl mx-auto py-12 px-6">
        <header className="mb-8 flex items-center justify-between">
          <h1 className="text-4xl font-extrabold tracking-tight">Usuarios</h1>
          <div className="flex items-center gap-4">
            {error && (
              <p className="text-sm text-red-600">
                {error}
              </p>
            )}
            <button
              onClick={openAdd}
              disabled={loading}
              className="inline-flex items-center gap-2 rounded-md bg-indigo-600 px-4 py-2 text-white shadow hover:bg-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Cargando...' : 'Agregar usuario'}
            </button>
          </div>
        </header>

        <div className="overflow-x-auto rounded-lg border border-gray-200 bg-white shadow-sm">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <Th>Nombre</Th>
                <Th>Apellido paterno</Th>
                <Th>Apellido materno</Th>
                <Th>Correo</Th>
                <Th>Teléfono</Th>
                <Th className="text-right">Acciones</Th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white">
              {loading ? (
                <tr>
                  <td colSpan={6} className="p-6 text-center text-gray-500">
                    Cargando usuarios...
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={6} className="p-6 text-center text-gray-500">
                    No hay usuarios
                  </td>
                </tr>
              ) : (
                users.map((u) => (
                  <tr key={u.id} className="hover:bg-gray-50">
                    <Td>{u.name}</Td>
                    <Td>{u.lastname}</Td>
                    <Td>{u.surname}</Td>
                    <Td>{u.email}</Td>
                    <Td>{u.tel}</Td>
                    <Td>
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => openEdit(u)}
                          disabled={loading}
                          className="rounded-md border border-gray-200 px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          Editar
                        </button>
                        <button
                          onClick={() => onDelete(u.id)}
                          disabled={loading}
                          className="rounded-md border border-red-200 px-3 py-1.5 text-sm text-red-700 hover:bg-red-50 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          Eliminar
                        </button>
                      </div>
                    </Td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-lg rounded-lg bg-white p-6 shadow-lg">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-lg font-medium">
                {editingId ? 'Editar usuario' : 'Agregar usuario'}
              </h2>
              <button
                onClick={close}
                className="rounded-md p-2 text-gray-500 hover:bg-gray-100"
                aria-label="Cerrar"
              >
                ✕
              </button>
            </div>

            <form onSubmit={onSubmit} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <Field label="Nombre">
                <input
                  name="name"
                  value={form.name}
                  onChange={onChange}
                  required
                  className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </Field>
              <Field label="Apellido paterno">
                <input
                  name="lastname"
                  value={form.lastname}
                  onChange={onChange}
                  required
                  className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </Field>
              <Field label="Apellido materno">
                <input
                  name="surname"
                  value={form.surname}
                  onChange={onChange}
                  required
                  className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </Field>
              <Field label="Correo">
                <input
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={onChange}
                  required
                  className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </Field>
              <Field label="Teléfono">
                <input
                  name="tel"
                  value={form.tel}
                  onChange={onChange}
                  required
                  className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </Field>

              <div className="col-span-full mt-2 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={close}
                  className="rounded-md border border-gray-200 px-4 py-2 text-gray-700 hover:bg-gray-100"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-500"
                >
                  {editingId ? 'Guardar' : 'Agregar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

function Th({ children, className = '' }) {
  return (
    <th
      className={`px-4 py-3 text-left text-sm font-semibold text-gray-700 ${className}`}
      scope="col"
    >
      {children}
    </th>
  );
}

function Td({ children, className = '' }) {
  return <td className={`px-4 py-3 text-sm text-gray-700 ${className}`}>{children}</td>;
}

function Field({ label, children }) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-gray-700">{label}</span>
      {children}
    </label>
  );
}
